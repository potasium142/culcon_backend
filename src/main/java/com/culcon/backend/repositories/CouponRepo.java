package com.culcon.backend.repositories;

import com.culcon.backend.models.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepo extends JpaRepository<Coupon, String> {
	List<Coupon> findAllByMinimumPriceLessThan(Float minimumPrice);
}
