package com.culcon.backend.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
	private Long id;

	@MapsId
	@JoinColumn(name = "order_id")
	@OneToOne(fetch = FetchType.LAZY)
	private OrderHistory order;

	@Column(name = "status")
	@Builder.Default
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private PaymentStatus status = PaymentStatus.CREATED;

	@Column(name = "payment_id")
	@Builder.Default
	private String paymentId = null;

	@Column(name = "refund_id")
	@Builder.Default
	private String refundId = null;

	@Column(name = "transaction_id")
	private String transactionId;

	@Column(name = "amount")
	private Float amount;

	@Column(name = "create_time")
	@Builder.Default
	private Timestamp createTime = Timestamp.valueOf(LocalDateTime.now());
}
