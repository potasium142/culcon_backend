package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.OrderCreation;
import com.culcon.backend.dtos.OrderSummary;
import com.culcon.backend.models.user.Coupon;
import com.culcon.backend.models.user.OrderHistory;
import com.culcon.backend.models.user.OrderHistoryItem;
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

			totalPrice += prod.getPrice() * entry.getValue();

			if (entry.getValue() > 0)
				productList.add(
					OrderHistoryItem.builder()
						.productId(prod)
						.quantity(entry.getValue())
						.build());
		}

		Coupon coupon = null;

		if (orderCreation.couponId() != null)
			coupon = couponRepo.findById(orderCreation.couponId()).orElse(null);


		if (coupon != null) {
			var isCouponUsable = coupon.getUsageLeft() > 0 && !coupon.getExpireTime().isBefore(LocalDate.now());

			if (isCouponUsable) {
				totalPrice = totalPrice * (1.0f - coupon.getSalePercent() / 100.0f);

				coupon.setUsageLeft(coupon.getUsageLeft() - 1);

				couponRepo.save(coupon);
			} else
				coupon = null;
		}


		var order = OrderHistory.builder()
			.user(account)
			.items(productList)
			.coupon(coupon)
			.totalPrice(totalPrice)
			.note(orderCreation.note())
			.deliveryAddress(orderCreation.deliveryAddress())
			.build();

		order = orderHistoryRepo.save(order);

		return OrderSummary.from(order);
	}
}
