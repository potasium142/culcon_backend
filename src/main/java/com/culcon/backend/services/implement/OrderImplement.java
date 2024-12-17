package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.order.*;
import com.culcon.backend.exceptions.custom.RuntimeExceptionPlusPlus;
import com.culcon.backend.models.Coupon;
import com.culcon.backend.models.OrderHistory;
import com.culcon.backend.models.OrderHistoryItem;
import com.culcon.backend.models.OrderStatus;
import com.culcon.backend.repositories.CouponRepo;
import com.culcon.backend.repositories.OrderHistoryRepo;
import com.culcon.backend.repositories.ProductPriceRepo;
import com.culcon.backend.repositories.ProductRepo;
import com.culcon.backend.services.OrderService;
import com.culcon.backend.services.PaymentService;
import com.culcon.backend.services.authenticate.AuthService;
import com.paypal.sdk.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
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

	private Coupon getCoupon(String id) {
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
	public OrderSummary createOrder(OrderCreation orderCreation, HttpServletRequest req) {
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

			cart.remove(prod);

			prod.setAvailableQuantity(prod.getAvailableQuantity() - entry.getValue());

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

		Coupon coupon = null;

		if (!orderCreation.couponId().isBlank()) {
			coupon = getCoupon(orderCreation.couponId());

			totalPrice = totalPrice * (1.0f - coupon.getSalePercent() / 100.0f);

			coupon.setUsageLeft(coupon.getUsageLeft() - 1);

			couponRepo.save(coupon);
		}


		var order = OrderHistory.builder()
			.user(account)
			.items(productList)
			.coupon(coupon)
			.totalPrice(totalPrice)
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

		return OrderSummary.from(order);
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
	public OrderDetail getOrderItem(HttpServletRequest req, Long orderId) {
		var account = authService.getUserInformation(req);

		var order = orderHistoryRepo.findByIdAndUser(orderId, account)
			.orElseThrow(() -> new NoSuchElementException("Order not found"));

		return OrderDetail.builder()
			.summary(OrderSummary.from(order))
			.items(order.getItems().stream().map(OrderItem::from).toList())
			.build();

	}

	@Override
	public OrderDetail updateOrder(HttpServletRequest req, Long orderId, OrderCreation orderCreation) {
		var account = authService.getUserInformation(req);


		var order = orderHistoryRepo.findByIdAndUser(orderId, account)
			.orElseThrow(() -> new NoSuchElementException("Order not found"));

		if (order.getOrderStatus() != OrderStatus.ON_CONFIRM) {
			throw new IllegalArgumentException("Order can only be edit on confirm status");
		}

		var totalPrice = order.getTotalPrice();

		Coupon coupon = getCoupon(orderCreation.couponId());

		var oldCouponId = order.getCoupon() == null ? "" : order.getCoupon().getId();

		var sameCoupon = oldCouponId.equals(coupon.getId());

		if (!sameCoupon) {

			for (OrderHistoryItem item : order.getItems()) {
				var prod = item.getProductId();

				totalPrice = prod.getPrice() * (1.0f - prod.getSalePercent() / 100.0f) * item.getQuantity();
			}

			totalPrice = totalPrice * (1.0f - coupon.getSalePercent() / 100.0f);

			coupon.setUsageLeft(coupon.getUsageLeft() - 1);

			couponRepo.save(coupon);
		}

		order.setTotalPrice(totalPrice);
		order.setCoupon(coupon);
		order.setPaymentMethod(orderCreation.paymentMethod());
		order.setDeliveryAddress(orderCreation.deliveryAddress().isBlank()
			? account.getAddress() : orderCreation.deliveryAddress());
		order.setPhonenumber(orderCreation.phoneNumber().isBlank()
			? account.getPhone() : orderCreation.phoneNumber());
		order.setReceiver(orderCreation.receiver().isBlank()
			? account.getUsername() : orderCreation.receiver());
		order.setNote(orderCreation.note());

		order = orderHistoryRepo.save(order);

		return OrderDetail.builder()
			.summary(OrderSummary.from(order))
			.items(order.getItems().stream().map(OrderItem::from).toList())
			.build();
	}

	@Override
	public OrderDetail cancelOrder(HttpServletRequest req, Long orderId) {
		var account = authService.getUserInformation(req);

		var order = orderHistoryRepo.findByIdAndUser(orderId, account)
			.orElseThrow(() -> new NoSuchElementException("Order not found"));

		if (order.getOrderStatus() != OrderStatus.ON_CONFIRM) {
			throw new IllegalArgumentException("Order can only be cancelled on confirm status");
		}

		order.getItems().forEach(orderItem -> {
			var product = orderItem.getProductId().getId().getProduct();
			product.setAvailableQuantity(product.getAvailableQuantity() + orderItem.getQuantity());
			productRepo.save(product);
		});

		order.setOrderStatus(OrderStatus.CANCELLED);
		order = orderHistoryRepo.save(order);

		try {
			paymentService.refund(order);
		} catch (IOException | ApiException e) {
			throw new RuntimeException(e);
		}

		return OrderDetail.builder()
			.items(order.getItems().stream().map(OrderItem::from).toList())
			.summary(OrderSummary.from(order))
			.build();
	}
}
