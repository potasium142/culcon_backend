package com.culcon.backend.dtos.order;

import com.culcon.backend.models.PaymentMethod;
import lombok.Builder;

import java.util.Map;

@Builder
public record OrderCreation(
	String couponId,
	String deliveryAddress,
	String phoneNumber,
	String receiver,
	String note,
	PaymentMethod paymentMethod,
	Map<String, Integer> product
) {
}
