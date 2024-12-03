package com.culcon.backend.dtos;

import com.culcon.backend.models.record.Product;
import lombok.Builder;

@Builder
public record CartItemDTO(
	Product product,
	Integer amount
) {
}
