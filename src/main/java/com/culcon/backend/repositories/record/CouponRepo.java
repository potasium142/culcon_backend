package com.culcon.backend.repositories.record;

import com.culcon.backend.models.record.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepo extends JpaRepository<Coupon, String> {
}
