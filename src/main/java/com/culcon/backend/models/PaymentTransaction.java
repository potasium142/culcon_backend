package com.culcon.backend.models;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

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
	private String id;

	@OneToOne
	private OrderHistory order;

	@Column(name = "status")
	private PaymentStatus status;

	@Column(name = "payment_id")
	private String paymentId;

	@Column(name = "amount")
	private Float amount;

	@Column(name = "create_time")
	private Timestamp createTime;
}
