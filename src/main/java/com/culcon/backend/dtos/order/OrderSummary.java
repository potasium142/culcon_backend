package com.culcon.backend.dtos.order;

import com.culcon.backend.models.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderSummary(
	Long id,
	LocalDateTime date,
	OrderStatus status,
	Float totalPrice,
	Coupon coupon,
	PaymentMethod paymentMethod,
	PaymentStatus paymentStatus,
	String deliveryAddress,
	String note
) {
	public static OrderSummary from(OrderHistory order) {
		return OrderSummary.builder()
			.id(order.getId())
			.date(order.getDate())
			.status(order.getOrderStatus())
			.totalPrice(order.getTotalPrice())
			.coupon(order.getCoupon())
			.deliveryAddress(order.getDeliveryAddress())
			.note(order.getNote())
			.paymentMethod(order.getPaymentMethod())
			.paymentStatus(order.getPaymentStatus())
			.build();
	}
}
