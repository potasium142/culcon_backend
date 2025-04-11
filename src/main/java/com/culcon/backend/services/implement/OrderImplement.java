package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.order.*;
import com.culcon.backend.exceptions.custom.RuntimeExceptionPlusPlus;
import com.culcon.backend.models.*;
import com.culcon.backend.repositories.*;
import com.culcon.backend.services.OrderService;
import com.culcon.backend.services.PaymentService;
import com.culcon.backend.services.authenticate.AuthService;
import com.paypal.sdk.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderImplement implements OrderService {

	private final AuthService authService;
	private final ProductPriceRepo productPriceRepo;
	private final CouponRepo couponRepo;
	private final OrderHistoryRepo orderHistoryRepo;
	private final ProductRepo productRepo;
	private final PaymentService paymentService;
	private final PaymentTransactionRepo paymentTransactionRepo;
	private final TaskScheduler taskScheduler;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Integer orderTimeout = 15;

	private Coupon getCoupon(String id) {

		if (id.isBlank())
			return null;

		var coupon = couponRepo.findById(id).orElseThrow(
			() -> new NoSuchElementException("Coupon Not Found")
		);

		if (coupon.getUsageLeft() <= 0)
			throw new RuntimeException("Coupon ran out of usages");

		if (coupon.getExpireTime().isBefore(LocalDate.now()))
			throw new RuntimeException("Coupon expired");

		return coupon;
	}

	@Override
	public OrderSummary createOrder(OrderCreation orderCreation, HttpServletRequest req) throws IOException, ApiException {
		//stinky ass code x2
		var account = authService.getUserInformation(req);

		var totalPrice = 0.0f;

		var productList = new ArrayList<OrderHistoryItem>();

		var cart = account.getCart();

		var notExistProduct = new ArrayList<String>();

		var insufficientAmount = new ArrayList<String>();

		var productNotInCart = new ArrayList<String>();

		var ohNo = false;

		for (Map.Entry<String, Integer> entry : orderCreation.product().entrySet()) {

			var prodPrice = productPriceRepo.findFirstById_ProductIdOrderById_DateDesc(entry.getKey());

			if (entry.getValue() <= 0) continue;

			if (prodPrice.isEmpty()) {
				notExistProduct.add(entry.getKey());
				ohNo = true;
				continue;
			}

			var prod = prodPrice.get().getId().getProduct();

			if (prod.getAvailableQuantity() < entry.getValue()) {
				insufficientAmount.add(entry.getKey());
				ohNo = true;
				continue;
			}

			if (!cart.containsKey(prod)) {
				productNotInCart.add(entry.getKey());
				ohNo = true;
				continue;
			}

			if (ohNo) continue;

			var amountLeft = cart.get(prod) - entry.getValue();

			if (amountLeft > 0) {
				cart.put(prod, amountLeft);
			} else {
				cart.remove(prod);
			}

			prod.setAvailableQuantity(prod.getAvailableQuantity() - entry.getValue());

			if (orderCreation.paymentMethod() == PaymentMethod.COD) {
				if (prod.getAvailableQuantity() <= 0) {
					prod.setProductStatus(ProductStatus.OUT_OF_STOCK);
				}
			}

			productRepo.save(prod);

			totalPrice += prod.getPrice() * (1.0f - prod.getSalePercent() / 100.0f) * entry.getValue();

			productList.add(
				OrderHistoryItem.builder()
					.productId(prodPrice.get())
					.quantity(entry.getValue())
					.build());

		}

		if (ohNo) {
			throw new RuntimeExceptionPlusPlus("Error occur during checkout",
				Map.of(
					"Non-exist product", notExistProduct,
					"Insufficient amount", insufficientAmount,
					"Product not in cart", productNotInCart
				)
			);
		}

		if (productList.isEmpty()) {
			throw new RuntimeException("Order does not contain any products");
		}

		Coupon coupon = getCoupon(orderCreation.couponId());

		if (coupon != null) {
			if (coupon.getMinimumPrice() < totalPrice) {

				totalPrice = totalPrice * (1.0f - coupon.getSalePercent() / 100.0f);

				coupon.setUsageLeft(coupon.getUsageLeft() - 1);

				couponRepo.save(coupon);
			}
		}

		var totalPriceRounded = Math.round(totalPrice * 100) / 100.0f;

		var order = OrderHistory.builder()
			.user(account)
			.items(productList)
			.coupon(coupon)
			.totalPrice(totalPriceRounded)
			.note(orderCreation.note())
			.paymentMethod(orderCreation.paymentMethod())
			.deliveryAddress(orderCreation.deliveryAddress().isBlank()
				? account.getAddress() : orderCreation.deliveryAddress())
			.receiver(orderCreation.receiver().isBlank()
				? account.getUsername() : orderCreation.receiver())
			.phonenumber(orderCreation.phoneNumber().isBlank()
				? account.getPhone() : orderCreation.phoneNumber())
			.build();

		order = orderHistoryRepo.save(order);

		switch (orderCreation.paymentMethod()) {
			case PAYPAL -> paymentService.createPayment(order, req);
			case VNPAY -> paymentService.createPaymentVNPay(order, "NCB", req);
			case COD -> {

			}
		}


		if (orderCreation.paymentMethod() != PaymentMethod.COD) {
			schedulePaymentCheck(order, req);
		}

		return OrderSummary.from(order);
	}

	public void schedulePaymentCheck(OrderHistory order, HttpServletRequest req) {
		Instant executionTime = Instant.now().plus(Duration.ofMinutes(15));

		taskScheduler.schedule(() -> {
			try {
				checkOrderPaymentStatus(order.getId());
			} catch (Exception e) {
				// Log the exception
				System.err.println("Error checking payment status for order " + order.getId() + ": " + e.getMessage());
			}
		}, executionTime);

		logger.info("Scheduled payment check for order {} at {}", order.getId(), executionTime);
	}

	private void checkOrderPaymentStatus(String orderId) {
		logger.info("Checking payment status for order {} at {}", orderId, Instant.now());
		OrderHistory order = orderHistoryRepo.findById(orderId)
			.orElseThrow(() -> new NoSuchElementException("Order not found"));

		if (order.getOrderStatus() == OrderStatus.ON_CONFIRM) {
			var isPaid = order.getPaymentStatus() == PaymentStatus.RECEIVED;
			var isCod = order.getPaymentMethod() == PaymentMethod.COD;


			if (!(isPaid || isCod)) {
				cancelOrder(order);
				logger.info("Order {} cancelled", orderId);
			}
		}
	}

	private OrderHistory getOrderForUpdate(String orderId, HttpServletRequest req) {
		var account = authService.getUserInformation(req);

		var order = orderHistoryRepo.findByIdAndUser(orderId, account)
			.orElseThrow(() -> new NoSuchElementException("Order not found"));

		if (order.getOrderStatus() != OrderStatus.ON_CONFIRM) {
			throw new RuntimeException("Order status is not on confirm");
		}

		return order;
	}

	@Override
	public List<OrderInList> getListOfOrderByStatus(HttpServletRequest req, OrderStatus status) {
		var account = authService.getUserInformation(req);

		return orderHistoryRepo.findByUserAndOrderStatus(account, status)
			.stream().map(OrderInList::from).toList();
	}

	@Override
	public List<OrderInList> getListOfAllOrder(HttpServletRequest req) {
		var account = authService.getUserInformation(req);

		return orderHistoryRepo.findByUser(account)
			.stream().map(OrderInList::from).toList();
	}

	@Override
	public OrderDetail getOrderDetail(HttpServletRequest req, String orderId) {
		var account = authService.getUserInformation(req);

		var order = orderHistoryRepo.findByIdAndUser(orderId, account)
			.orElseThrow(() -> new NoSuchElementException("Order not found"));
		return OrderDetail.builder()
			.summary(OrderSummary.from(order))
			.items(order.getItems().stream().map(OrderItem::from).toList())
			.build();

	}

	//
//	@Override
//	public CouponDTO updateOrderCoupon(HttpServletRequest req, String orderId, String couponId) throws IOException, ApiException {
//		var order = getOrderForUpdate(orderId, req);
//
//		if (order.getUpdatedCoupon()) {
//			throw new RuntimeException("Order can only update once");
//		}
//
//		var oldCoupon = order.getCoupon();
//
//		var newCoupon = getCoupon(couponId);
//
//		if (oldCoupon == newCoupon) {
//			throw new RuntimeException("No coupon was change");
//		}
//
//		var price = order.getTotalPrice();
//
//		if (oldCoupon == null) {
//			price = order.getTotalPrice() * (1.0f - newCoupon.getSalePercent() / 100.0f);
//		} else {
//			price = order.getTotalPrice() / (1.0f - oldCoupon.getSalePercent() / 100.0f);
//
//			// stinky ass code cuz im sick as hell
//			if (newCoupon == null) {
//				order.setTotalPrice(price);
//			} else if (newCoupon.getMinimumPrice() > price) {
//				order.setTotalPrice(price);
//			} else {
//				price = price * (1.0f - newCoupon.getSalePercent() / 100.0f);
//				order.setTotalPrice(price);
//			}
//		}
//
//		order.setTotalPrice(price);
//		order.setCoupon(newCoupon);
//		order.setUpdatedCoupon(true);
//
//		paymentService.updatePrice(order, price);
//
//		return CouponDTO.from(order.getCoupon());
//	}
//
	//
//	@Override
//	public OrderSummary changePayment(HttpServletRequest req,
//	                                  String orderId, PaymentMethod paymentMethod)
//		throws IOException, ApiException {
//		var order = getOrderForUpdate(orderId, req);
//
//		if (order.getPaymentMethod() == paymentMethod) {
//			throw new RuntimeException("Payment method was not changed");
//		}
//
//		if (order.getUpdatedPayment()) {
//			throw new RuntimeException("Payment method can only update once");
//		}
//
//		var pt = paymentTransactionRepo.findByOrder(order);
//
//		if (pt.isPresent()) {
//			if (pt.get().getStatus() != PaymentStatus.CREATED) {
//				throw new RuntimeException("Payment method can only be changed before order was paid");
//			}
//		}
//
//		switch (paymentMethod) {
//			case PAYPAL -> paymentService.createPayment(order, req);
//			case VNPAY -> paymentService.createPaymentVNPay(order, "NCB", req);
//			case COD -> pt.ifPresent(paymentTransactionRepo::delete);
//		}
//
//		order.setPaymentMethod(paymentMethod);
//		order.setUpdatedPayment(true);
//
//		orderHistoryRepo.save(order);
//
//		return OrderSummary.from(order);
//	}
//
	@Override
	public OrderSummary updateOrder(HttpServletRequest req, String orderId, OrderUpdate orderCreation) {
		var account = authService.getUserInformation(req);

		var order = orderHistoryRepo.findByIdAndUser(orderId, account)
			.orElseThrow(() -> new NoSuchElementException("Order not found"));

		order.setDeliveryAddress(orderCreation.deliveryAddress().isBlank()
			? account.getAddress() : orderCreation.deliveryAddress());
		order.setPhonenumber(orderCreation.phoneNumber().isBlank()
			? account.getPhone() : orderCreation.phoneNumber());
		order.setReceiver(orderCreation.receiver().isBlank()
			? account.getUsername() : orderCreation.receiver());
		order.setNote(orderCreation.note());

		order = orderHistoryRepo.save(order);

		return OrderSummary.from(order);
	}

	public OrderSummary cancelOrder(OrderHistory order) {
		if (order.getOrderStatus() != OrderStatus.ON_CONFIRM) {
			throw new IllegalArgumentException("Order can only be cancelled on confirm status");
		}

		Hibernate.initialize(order.getItems());

		order.getItems().forEach(orderItem -> {
			var product = orderItem.getProductId().getId().getProduct();
			product.setAvailableQuantity(product.getAvailableQuantity() + orderItem.getQuantity());
			product.setProductStatus(ProductStatus.IN_STOCK);
			productRepo.save(product);
		});

		order.setOrderStatus(OrderStatus.CANCELLED);
		order = orderHistoryRepo.save(order);

		try {
			paymentService.refund(order);
		} catch (IOException | ApiException e) {
			throw new RuntimeException(e);
		}

		return OrderSummary.from(order);
	}

	@Override
	public OrderSummary cancelOrder(HttpServletRequest req, String orderId) {
		var account = authService.getUserInformation(req);

		var order = orderHistoryRepo.findByIdAndUser(orderId, account)
			.orElseThrow(() -> new NoSuchElementException("Order not found"));

		return cancelOrder(order);
	}

	@Override
	public OrderSummary receiveOrder(HttpServletRequest req, String orderId) {
		var account = authService.getUserInformation(req);

		var order = orderHistoryRepo.findByIdAndUser(orderId, account)
			.orElseThrow(() -> new NoSuchElementException("Order not found"));


		if (order.getOrderStatus() != OrderStatus.SHIPPED) {
			throw new IllegalArgumentException("Only delivered orders can be received");
		}

		if (order.getPaymentMethod() == PaymentMethod.COD) {
			var payment = PaymentTransaction.builder()
				.order(order)
				.amount(order.getTotalPrice())
				.status(PaymentStatus.RECEIVED)
				.createTime(Timestamp.valueOf(LocalDateTime.now())).build();

			paymentTransactionRepo.save(payment);
		}

		order.setOrderStatus(OrderStatus.DELIVERED);
		order = orderHistoryRepo.save(order);

		return OrderSummary.from(order);
	}
}
