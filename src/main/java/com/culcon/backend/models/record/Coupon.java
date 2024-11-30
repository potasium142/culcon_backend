package com.culcon.backend.models.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "coupon")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
	@Id
	private String id;

	@Column(name = "expire_time")
	@Builder.Default
	private LocalDate expireTime = LocalDate.now().plusDays(7);

	@Column(name = "sale_percent")
	@Builder.Default
	private Float salePercent = 0.0f;

	@Column(name = "usage_amount")
	@Builder.Default
	private Integer usageAmount = Integer.MAX_VALUE;

	@Column(name = "usage_left")
	@Builder.Default
	private Integer usageLeft = Integer.MAX_VALUE;
}
