package com.culcon.backend.dtos;

import com.culcon.backend.models.Coupon;
import lombok.Builder;

@Builder
public record CouponDTO(
	String id,
	Float salePercent
) {
	public static CouponDTO from(Coupon coupon) {
		if (coupon == null) {
			return CouponDTO.builder()
				.id("Empty coupon")
				.salePercent(0.0f)
				.build();
		}

		return CouponDTO.builder()
			.id(coupon.getId())
			.salePercent(coupon.getSalePercent())
			.build();
	}
}
