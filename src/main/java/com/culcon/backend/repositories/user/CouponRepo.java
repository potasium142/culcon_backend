package com.culcon.backend.repositories.user;

import com.culcon.backend.models.user.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepo extends JpaRepository<Coupon, String> {
}
