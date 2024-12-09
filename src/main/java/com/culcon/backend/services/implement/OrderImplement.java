package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.order.*;
import com.culcon.backend.models.Coupon;
import com.culcon.backend.models.OrderHistory;
import com.culcon.backend.models.OrderHistoryItem;
import com.culcon.backend.models.OrderStatus;
import com.culcon.backend.repositories.CouponRepo;
import com.culcon.backend.repositories.OrderHistoryRepo;
import com.culcon.backend.repositories.ProductPriceRepo;
import com.culcon.backend.services.OrderService;
import com.culcon.backend.services.authenticate.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderImplement implements OrderService {

	private final AuthService authService;
	private final ProductPriceRepo productPriceRepo;
	private final CouponRepo couponRepo;
	private final OrderHistoryRepo orderHistoryRepo;

	private Coupon getCoupon(String id) {
		var coupon = couponRepo.findById(id).orElse(null);

		if (coupon != null) {
			var isCouponUsable = coupon.getUsageLeft() > 0 && !coupon.getExpireTime().isBefore(LocalDate.now());

			return isCouponUsable ? coupon : null;
		}

		return null;
	}

	@Override
	public OrderSummary createOrder(OrderCreation orderCreation, HttpServletRequest req) {
		var account = authService.getUserInformation(req);

		var totalPrice = 0.0f;

		var productList = new ArrayList<OrderHistoryItem>();

		var cart = account.getCart();

		var runtimeExceptionPlusPlusTable = new HashMap<Object, Object>();

		var

		for (Map.Entry<String, Integer> entry : orderCreation.product().entrySet()) {

			var prodPrice = productPriceRepo.findFirstById_ProductIdOrderById_DateDesc(entry.getKey());


			var prod = prodPrice.getId().getProduct();

			if (prod.getAvailableQuantity() < entry.getValue()) {
				throw new RuntimeException(
					String.format("Product {%s} does not have enough stock", entry.getKey()));
			}

			cart.remove(prod);

			totalPrice += prodPrice.getPrice() * (1.0f - prodPrice.getSalePercent() / 100.0f) * entry.getValue();

			if (entry.getValue() > 0)
				productList.add(
					OrderHistoryItem.builder()
						.productId(prodPrice)
						.quantity(entry.getValue())
						.build());
		}

		Coupon coupon = getCoupon(orderCreation.couponId());

		if (coupon != null) {
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
			.deliveryAddress(orderCreation.deliveryAddress())
			.receiver(orderCreation.receiver())
			.phonenumber(orderCreation.phoneNumber())
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

		if (coupon != null) {

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

		}

		order.setTotalPrice(totalPrice);
		order.setCoupon(coupon);
		order.setPaymentMethod(orderCreation.paymentMethod());
		order.setDeliveryAddress(orderCreation.deliveryAddress());
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

		order.setOrderStatus(OrderStatus.CANCELLED);
		order = orderHistoryRepo.save(order);

		return OrderDetail.builder()
			.items(order.getItems().stream().map(OrderItem::from).toList())
			.summary(OrderSummary.from(order))
			.build();
	}
}
