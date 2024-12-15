package com.culcon.backend.dtos.order;

import com.culcon.backend.models.OrderHistory;
import com.culcon.backend.models.OrderStatus;
import com.culcon.backend.models.PaymentMethod;
import com.culcon.backend.models.PaymentStatus;
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
			.items(order.getItems().stream().limit(2)
				.map(OrderItem::from)
				.toList())
			.totalItems(order.getItems().size())
			.build();
	}
}
