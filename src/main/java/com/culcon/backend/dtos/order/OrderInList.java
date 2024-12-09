package com.culcon.backend.dtos.order;

import com.culcon.backend.models.user.OrderHistory;
import com.culcon.backend.models.user.OrderStatus;
import com.culcon.backend.models.user.PaymentMethod;
import com.culcon.backend.models.user.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderInList(
	Long id,
	LocalDateTime date,
	OrderStatus status,
	Float totalPrice,
	PaymentMethod paymentMethod,
	PaymentStatus paymentStatus,
	List<OrderItem> items,
	Integer totalItems
) {
	public static OrderInList from(OrderHistory order) {
		return OrderInList.builder()
			.id(order.getId())
			.date(order.getDate())
			.status(order.getOrderStatus())
			.totalPrice(order.getTotalPrice())
			.paymentMethod(order.getPaymentMethod())
			.paymentStatus(order.getPaymentStatus())
			.items(order.getItems().subList(0, 2).stream()
				.map(OrderItem::from)
				.toList())
			.totalItems(order.getItems().size())
			.build();
	}
}
