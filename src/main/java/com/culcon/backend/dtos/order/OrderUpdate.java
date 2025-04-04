package com.culcon.backend.dtos.order;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record OrderUpdate(
	String deliveryAddress,
	@Pattern(regexp = "0[1-9][0-9]{8,9}")
	String phoneNumber,
	String receiver,
	String note
) {
}

