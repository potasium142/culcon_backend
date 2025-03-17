package com.culcon.backend.dtos;

import com.culcon.backend.models.PaymentStatus;
import com.culcon.backend.models.PaymentTransaction;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record PaymentDTO(
	String id,
	PaymentStatus status,
	String transactionId,
	Float amount,
	Timestamp createTime
) {
	public static PaymentDTO from(PaymentTransaction pt) {
		return PaymentDTO.builder()
			.id(pt.getOrder().getId())
			.status(pt.getStatus())
			.amount(pt.getAmount())
			.transactionId(pt.getTransactionId())
			.createTime(pt.getCreateTime())
			.build();
	}
}
