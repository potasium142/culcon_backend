package com.culcon.backend.dtos.order;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderDetail(
	OrderSummary summary,
	List<OrderItem> items
) {
}
