package com.culcon.backend.repositories;

import com.culcon.backend.models.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepo extends JpaRepository<Coupon, String> {
}
