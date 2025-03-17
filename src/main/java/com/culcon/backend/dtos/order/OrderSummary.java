package com.culcon.backend.dtos.order;

import com.culcon.backend.dtos.CouponDTO;
import com.culcon.backend.models.OrderHistory;
import com.culcon.backend.models.OrderStatus;
import com.culcon.backend.models.PaymentMethod;
import com.culcon.backend.models.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderSummary(
	String id,
	LocalDateTime date,
	OrderStatus status,
	Float totalPrice,
	CouponDTO coupon,
	PaymentMethod paymentMethod,
	PaymentStatus paymentStatus,
	String phoneNumber,
	String receiver,
	String deliveryAddress,
	String note
) {
	public static OrderSummary from(OrderHistory order) {
		return OrderSummary.builder()
			.id(order.getId())
			.date(order.getDate())
			.status(order.getOrderStatus())
			.totalPrice(order.getTotalPrice())
			.coupon(CouponDTO.from(order.getCoupon()))
			.deliveryAddress(order.getDeliveryAddress())
			.note(order.getNote())
			.paymentMethod(order.getPaymentMethod())
			.paymentStatus(order.getPaymentStatus())
			.phoneNumber(order.getPhonenumber())
			.receiver(order.getReceiver())
			.build();
	}
}
