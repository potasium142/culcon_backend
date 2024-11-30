package com.culcon.backend.controllers;


import com.culcon.backend.models.docs.Blog;
import com.culcon.backend.models.record.Coupon;
import com.culcon.backend.models.record.Product;
import com.culcon.backend.repositories.docs.BlogDocRepo;
import com.culcon.backend.repositories.record.CouponRepo;
import com.culcon.backend.repositories.record.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {

	private final ProductRepo productRepo;
	private final CouponRepo couponRepo;
	private final BlogDocRepo blogRepo;

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

	@PostMapping("/record/blog/create")
	public Blog createBlog(
		@RequestBody Blog blog
	) {
		return blogRepo.save(blog);
	}
}
