package com.culcon.backend.dtos.order;

import com.culcon.backend.models.PaymentMethod;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.Map;

@Builder
public record OrderCreation(
	String couponId,
	String deliveryAddress,
	@Pattern(regexp = "0[1-9]{2}[0-9]{7}")
	String phoneNumber,
	String receiver,
	String note,
	PaymentMethod paymentMethod,
	Map<String, Integer> product
) {
}
