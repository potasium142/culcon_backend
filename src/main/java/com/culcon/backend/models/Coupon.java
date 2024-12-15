package com.culcon.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "coupon")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
	@Id
	private String id;

	@Column(name = "expire_time")
	private LocalDate expireTime;

	@Column(name = "sale_percent")
	private Float salePercent;

	@Column(name = "usage_amount")
	@Min(value = 0)
	private Integer usageAmount;

	@Column(name = "usage_left")
	@Min(value = 0)
	private Integer usageLeft;
}
