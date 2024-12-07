package com.culcon.backend.dtos;

import com.culcon.backend.models.user.Product;
import lombok.Builder;

import java.util.Map;

@Builder
public record CartItemDTO(
	Product product,
	Integer amount
) {
	public static CartItemDTO of(Map.Entry<Product, Integer> entry) {
		return new CartItemDTO(entry.getKey(), entry.getValue());
	}
}
