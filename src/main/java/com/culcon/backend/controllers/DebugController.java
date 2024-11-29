package com.culcon.backend.controllers;


import com.culcon.backend.models.docs.MealKitInfo;
import com.culcon.backend.models.record.Coupon;
import com.culcon.backend.models.record.Product;
import com.culcon.backend.repositories.docs.MealKitInfoDocRepo;
import com.culcon.backend.repositories.record.CouponRepo;
import com.culcon.backend.repositories.record.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {

	private final ProductRepo productRepo;
	private final CouponRepo couponRepo;
	private final MealKitInfoDocRepo mealKitInfoRepo;

	@GetMapping("/test_permission")
	public String permissionTest() {
		return "sucess";
	}

	@PostMapping("/record/product/create")
	public Product createProduct(
		@RequestBody Product product
	) {
		return productRepo.save(product);
	}

	@PostMapping("/record/coupon/create")
	public Coupon createCoupon(
		@RequestBody Coupon coupon
	) {
		return couponRepo.save(coupon);
	}

	@GetMapping("/docs/mealkit/fetch/all")
	public List<MealKitInfo> fetchAllProducts() {
		return mealKitInfoRepo.findAll();
	}
}
