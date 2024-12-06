package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.order.*;
import com.culcon.backend.models.user.Coupon;
import com.culcon.backend.models.user.OrderHistory;
import com.culcon.backend.models.user.OrderHistoryItem;
import com.culcon.backend.models.user.OrderStatus;
import com.culcon.backend.repositories.user.CouponRepo;
import com.culcon.backend.repositories.user.OrderHistoryRepo;
import com.culcon.backend.repositories.user.ProductPriceRepo;
import com.culcon.backend.services.OrderService;
import com.culcon.backend.services.authenticate.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

		for (Map.Entry<String, Integer> entry : orderCreation.product().entrySet()) {

			var prod = productPriceRepo.findFirstById_ProductIdOrderById_DateDesc(entry.getKey())
				.orElseThrow(() -> new NoSuchElementException("Product not found"));

			cart.remove(prod.getId().getProduct());

			totalPrice += prod.getPrice() * (1.0f - prod.getSalePercent() / 100.0f) * entry.getValue();

			if (entry.getValue() > 0)
				productList.add(
					OrderHistoryItem.builder()
						.productId(prod)
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
			.build();

		order = orderHistoryRepo.save(order);

		return OrderSummary.from(order);
	}

	@Override
	public List<OrderItemInList> getListOfOrder(HttpServletRequest req, OrderStatus status) {
		var account = authService.getUserInformation(req);

		return orderHistoryRepo.findByUserAndOrderStatus(account, status)
			.stream().map(OrderItemInList::from).toList();
	}

	@Override
	public List<OrderItemInList> getListOfAllOrder(HttpServletRequest req) {
		var account = authService.getUserInformation(req);

		return orderHistoryRepo.findByUser(account)
			.stream().map(OrderItemInList::from).toList();
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
