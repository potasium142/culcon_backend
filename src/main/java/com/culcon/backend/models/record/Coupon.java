package com.culcon.backend.models.record;

import com.culcon.backend.models.etc.CouponIdGenerator;
import jakarta.persistence.*;
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
	@GeneratedValue(generator = CouponIdGenerator.GENERATOR_NAME)
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
}
