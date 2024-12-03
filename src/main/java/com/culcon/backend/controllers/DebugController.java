package com.culcon.backend.controllers;


import com.culcon.backend.models.docs.Blog;
import com.culcon.backend.models.docs.MealKitDoc;
import com.culcon.backend.models.docs.ProductDoc;
import com.culcon.backend.models.record.Coupon;
import com.culcon.backend.models.record.Product;
import com.culcon.backend.models.record.ProductStatus;
import com.culcon.backend.models.record.ProductType;
import com.culcon.backend.repositories.docs.BlogDocRepo;
import com.culcon.backend.repositories.docs.MealKitDocRepo;
import com.culcon.backend.repositories.docs.ProductDocRepo;
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
	private final BlogDocRepo blogRepo;
	private final ProductDocRepo productDocRepo;
	private final MealKitDocRepo mealKitRepo;

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

	@PostMapping("/docs/blog/create")
	public Blog createBlog(
		@RequestBody Blog blog
	) {
		return blogRepo.save(blog);
	}

	@PostMapping("/docs/product/create")
	public ProductDoc createProductDoc(
		@RequestBody ProductDoc productDoc
	) {
		return productDocRepo.save(productDoc);
	}

	@PostMapping("/docs/mealkit/create")
	public MealKitDoc createMealKitDoc(
		@RequestBody MealKitDoc mealKitDoc
	) {
		return mealKitRepo.save(mealKitDoc);
	}

	@GetMapping("/docs/product/fetch")
	public List<ProductDoc> fetchProductDoc() {
		return productDocRepo.findAll();
	}

	@GetMapping("/docs/mealkit/fetch")
	public List<MealKitDoc> fetchMealKitDoc() {
		return mealKitRepo.findAll();
	}


	@GetMapping("/map")
	public void mapMongoToLocal() {
		productDocRepo.findAll().forEach(
			proDoc -> {
				var pro = Product.builder()
					.id(proDoc.getId())
					.availableQuantity(100)
					.productName(proDoc.getName())
					.productStatus(ProductStatus.IN_STOCK)
					.productTypes(ProductType.MEAT)
					.build();

				productRepo.save(pro);

			});

		mealKitRepo.findAll().forEach(
			mealKitDoc -> {
				var pro = Product.builder()
					.id(mealKitDoc.getId())
					.availableQuantity(100)
					.productName(mealKitDoc.getName())
					.productStatus(ProductStatus.IN_STOCK)
					.productTypes(ProductType.MEALKIT)
					.build();
				productRepo.save(pro);
			}
		);

	}
}
