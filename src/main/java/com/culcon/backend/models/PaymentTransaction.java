package com.culcon.backend.models;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction {
	@Id
	@Column(name = "order_id")
	private String id;

	@OneToOne
	@JoinColumn(name = "order_id")
	private OrderHistory order;

	@Column(name = "status")
	@Builder.Default
	private PaymentStatus status = PaymentStatus.CREATED;

	@Column(name = "payment_id")
	@Builder.Default
	private String paymentId = null;

	@Column(name = "transaction_id")
	private String transactionId;

	@Column(name = "amount")
	private Float amount;

	@Column(name = "create_time")
	@Builder.Default
	private Timestamp createTime = Timestamp.valueOf(LocalDateTime.now());
}
