package com.culcon.backend.controllers.guest;

import com.culcon.backend.models.ProductType;
import com.culcon.backend.services.PublicService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

	private final PublicService publicService;

	@Operation(tags = "Permission Test", summary = "Test permission for guest")
	@GetMapping("/test_permission")
	public String permissionTest() {
		return "sucess";
	}

	@Operation(
		tags = {"Product", "Public"}
	)
	@GetMapping("/fetch/product/all")
	public ResponseEntity<?> fetchAllProducts(
		@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
		@RequestParam(value = "pageSize", defaultValue = "7", required = false) int pageSize
	) {
		var pageable = PageRequest.of(pageNo, pageSize);
		return new ResponseEntity<>(publicService.fetchListOfProducts(pageable), HttpStatus.OK);
	}

	@Operation(
		tags = {"Product", "Public"}
	)
	@GetMapping("/fetch/product/{id}")
	public ResponseEntity<?> fetchProduct(@PathVariable String id) {
		return new ResponseEntity<>(publicService.fetchProduct(id), HttpStatus.OK);
	}

	@Operation(
		tags = {"Product", "Public"}
	)
	@GetMapping("/fetch/product/category/{category}")
	public ResponseEntity<?> fetchProduct(
		@PathVariable ProductType category,
		@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
		@RequestParam(value = "pageSize", defaultValue = "7", required = false) int pageSize
	) {
		var pageRequest = PageRequest.of(pageNo, pageSize);
		return new ResponseEntity<>(publicService.fetchListOfProductsByCategory(category, pageRequest), HttpStatus.OK);
	}

	@Operation(
		tags = {"Product", "Public", "Search"}
	)
	@GetMapping("/search/product")
	public ResponseEntity<?> searchProduct(
		@RequestParam String keyword,
		@RequestParam @Nullable ProductType type) {
		return ResponseEntity.ok(publicService.searchProduct(keyword, type));
	}

	@Operation(
		tags = {"Blog", "Public"}
	)
	@GetMapping("/fetch/blog/all")
	public ResponseEntity<?> fetchAllBlogs() {
		return ResponseEntity.ok(publicService.fetchListOfBlog());
	}

	@Operation(
		tags = {"Blog", "Public"}
	)
	@GetMapping("/fetch/blog/{id}")
	public ResponseEntity<?> fetchBlog(
		HttpServletRequest request,
		@PathVariable String id) {
		return ResponseEntity.ok(publicService.fetchBlogDetail(id, request));
	}

	@Operation(
		tags = {"Blog", "Public", "Comment"}
	)
	@GetMapping("/fetch/blog/comment")
	public ResponseEntity<?> fetchBlogComment(@RequestParam String id) {
		return ResponseEntity.ok(publicService.fetchBlogComment(id));
	}

	@Operation(
		tags = {"Blog", "Public", "Comment"}
	)
	@GetMapping("/fetch/blog/reply")
	public ResponseEntity<?> fetchCommentReply(String blogId, String commentId) {
		return ResponseEntity.ok(publicService.fetchReply(blogId, commentId));
	}

	@Operation(
		tags = {"Blog", "Public", "Search"}
	)
	@GetMapping("/search/blog")
	public ResponseEntity<?> searchBlogNorm(
		@RequestParam String keyword,
		@RequestParam @Nullable HashSet<String> tags) {
		return ResponseEntity.ok(publicService.searchBlogByTitle(keyword, tags));
	}

	@Operation(tags = {"Public", "Coupon"})
	@GetMapping("/fetch/coupon/all")
	public ResponseEntity<?> fetchAllCoupons() {
		return ResponseEntity.ok(publicService.fetchAllValidCoupon());
	}

	@Operation(tags = {"Public", "Coupon"})
	@GetMapping("/fetch/coupon")
	public ResponseEntity<?> fetchCoupon(
		@RequestParam String couponId
	) {
		return ResponseEntity.ok(publicService.fetchCoupon(couponId));
	}

	@Operation(tags = {"Public", "Coupon"})
	@GetMapping("/fetch/coupon/price")
	public ResponseEntity<?> fetchCouponByPrice(
		@RequestParam Float price
	) {
		return ResponseEntity.ok(publicService.fetchAllCouponForPrice(price));
	}


}
