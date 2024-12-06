package com.culcon.backend.dtos;

import lombok.Builder;

import java.util.Map;

@Builder
public record OrderCreation(
	String couponId,
	String deliveryAddress,
	String note,
	Map<String, Integer> product
) {
}
