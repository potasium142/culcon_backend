package com.culcon.backend.dtos;

import com.culcon.backend.models.Coupon;
import lombok.Builder;

@Builder
public record CouponDTO(
	String id,
	Float salePercent
) {
	public static CouponDTO from(Coupon coupon) {
		return coupon == null ? null : CouponDTO.builder()
			.id(coupon.getId())
			.salePercent(coupon.getSalePercent())
			.build();
	}
}
