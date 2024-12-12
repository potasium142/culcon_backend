package com.culcon.backend.controllers;


import com.cloudinary.Cloudinary;
import com.culcon.backend.models.*;
import com.culcon.backend.mongodb.model.MealKitDoc;
import com.culcon.backend.mongodb.model.ProductDoc;
import com.culcon.backend.mongodb.repository.MealKitDocRepo;
import com.culcon.backend.mongodb.repository.ProductDocRepo;
import com.culcon.backend.repositories.CouponRepo;
import com.culcon.backend.repositories.OrderHistoryRepo;
import com.culcon.backend.repositories.ProductPriceRepo;
import com.culcon.backend.repositories.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {

	private final Cloudinary cloudinary;
	private final ProductRepo productRepo;
	private final CouponRepo couponRepo;
	private final ProductDocRepo productDocRepo;
	private final MealKitDocRepo mealKitRepo;
	private final ProductPriceRepo productPriceRepo;
	private final OrderHistoryRepo orderHistoryRepo;

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

	@PostMapping("/order/update/status")
	public OrderHistory updateOrderStatus(
		@RequestParam Long orderId,
		@RequestParam OrderStatus status,
		@RequestParam PaymentStatus paymentStatus,
		@RequestParam PaymentMethod paymentMethod
	) {
		var order = orderHistoryRepo.findById(orderId).orElseThrow();
		order.setOrderStatus(status);
		order.setPaymentStatus(paymentStatus);
		order.setPaymentMethod(paymentMethod);
		return orderHistoryRepo.save(order);
	}

	@GetMapping("/order/get")
	public List<OrderHistory> getOrderHistory(
	) {
		return orderHistoryRepo.findAll();
	}

	@PostMapping("/record/coupon/create")
	public Coupon createCoupon(
		@RequestBody Coupon coupon
	) {
		return couponRepo.save(coupon);
	}

	@GetMapping("/docs/product/fetch")
	public List<ProductDoc> fetchProductDoc() {
		return productDocRepo.findAll();
	}

	@GetMapping("/docs/mealkit/fetch")
	public List<MealKitDoc> fetchMealKitDoc() {
		return mealKitRepo.findAll();
	}

	@GetMapping("/cloudinary/clear/user-pfp")
	public void clearUserPfp() throws Exception {
		cloudinary.api().deleteResourcesByPrefix("pfp", Map.of());
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
					.productTypes(proDoc.getType())
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
