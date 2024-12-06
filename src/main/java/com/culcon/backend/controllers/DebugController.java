package com.culcon.backend.controllers;


import com.culcon.backend.models.docs.Blog;
import com.culcon.backend.models.docs.MealKitDoc;
import com.culcon.backend.models.docs.ProductDoc;
import com.culcon.backend.models.user.*;
import com.culcon.backend.repositories.docs.BlogDocRepo;
import com.culcon.backend.repositories.docs.MealKitDocRepo;
import com.culcon.backend.repositories.docs.ProductDocRepo;
import com.culcon.backend.repositories.user.CouponRepo;
import com.culcon.backend.repositories.user.ProductPriceRepo;
import com.culcon.backend.repositories.user.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
	private final ProductPriceRepo productPriceRepo;

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

	@PostMapping("/record/product/price/create")
	public ProductPriceHistory createProductPriceHistory(
		@RequestParam String id,
		@RequestParam Float price
	) {
		var product = productRepo.findById(id).orElseThrow();
		var priceHistory = ProductPriceHistory.builder()
			.id(ProductPriceHistoryId.builder()
				.product(product)
				.date(LocalDateTime.now())
				.build()
			)
			.price(price)
			.salePercent(0.0f)
			.build();

		return productPriceRepo.save(priceHistory);
	}

	@GetMapping("/record/product/price/get")
	public ProductPriceHistory getProductPriceHistory(
		@RequestParam String id
	) {
		return productPriceRepo.findFirstById_ProductIdOrderById_DateDesc(id).orElseThrow();
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

				var imageUrl = proDoc.getImagesUrl().isEmpty() ?
					"" : proDoc.getImagesUrl().get(0);
				var pro = Product.builder()
					.id(proDoc.getId())
					.availableQuantity(100)
					.productName(proDoc.getName())
					.productStatus(ProductStatus.IN_STOCK)
					.productTypes(ProductType.MEAT)
					.imageUrl(imageUrl)
					.price(proDoc.getPrice())
					.salePercent(proDoc.getSalePercent())
					.build();

				productRepo.save(pro);

				var price = ProductPriceHistory.builder()
					.price(proDoc.getPrice())
					.salePercent(proDoc.getSalePercent())
					.id(ProductPriceHistoryId.builder()
						.date(LocalDateTime.now())
						.product(pro)
						.build())
					.build();

				productPriceRepo.save(price);
			});

		mealKitRepo.findAll().forEach(
			mealKitDoc -> {

				var imageUrl = mealKitDoc.getImagesUrl().isEmpty() ?
					"" : mealKitDoc.getImagesUrl().get(0);
				var pro = Product.builder()
					.id(mealKitDoc.getId())
					.availableQuantity(100)
					.productName(mealKitDoc.getName())
					.productStatus(ProductStatus.IN_STOCK)
					.productTypes(ProductType.MEALKIT)
					.imageUrl(imageUrl)
					.price(mealKitDoc.getPrice())
					.salePercent(mealKitDoc.getSalePercent())
					.build();

				productRepo.save(pro);

				var price = ProductPriceHistory.builder()
					.price(mealKitDoc.getPrice())
					.salePercent(mealKitDoc.getSalePercent())
					.id(ProductPriceHistoryId.builder()
						.date(LocalDateTime.now())
						.product(pro)
						.build())
					.build();

				productPriceRepo.save(price);
			}
		);

	}
}
