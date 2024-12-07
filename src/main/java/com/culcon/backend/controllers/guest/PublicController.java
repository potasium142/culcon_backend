package com.culcon.backend.controllers.guest;

import com.culcon.backend.models.user.ProductType;
import com.culcon.backend.services.PublicService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
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

	@GetMapping("/fetch/product/all")
	public ResponseEntity<?> fetchAllProducts() {
		return new ResponseEntity<>(publicService.fetchListOfProducts(), HttpStatus.OK);
	}

	@GetMapping("/fetch/product/{id}")
	public ResponseEntity<?> fetchProduct(@PathVariable String id) {
		return new ResponseEntity<>(publicService.fetchProduct(id), HttpStatus.OK);
	}

	@GetMapping("/search/product")
	public ResponseEntity<?> searchProduct(
		@RequestParam String keyword,
		@RequestParam @Nullable ProductType type) {
		return ResponseEntity.ok(publicService.searchProduct(keyword, type));
	}

	@GetMapping("/fetch/blog/all")
	public ResponseEntity<?> fetchAllBlogs() {
		return ResponseEntity.ok(publicService.fetchListOfBlog());
	}

	@GetMapping("/fetch/blog/{id}")
	public ResponseEntity<?> fetchBlog(@PathVariable String id) {
		return ResponseEntity.ok(publicService.fetchBlogDetail(id));
	}

	@GetMapping("/fetch/blog/comment/{id}")
	public ResponseEntity<?> fetchBlogComment(@PathVariable String id) {
		return ResponseEntity.ok(publicService.fetchBlogComment(id));
	}


	@GetMapping("/search/blog")
	public ResponseEntity<?> searchBlogNorm(
		@RequestParam String keyword,
		@RequestParam @Nullable HashSet<String> tags) {
		return ResponseEntity.ok(publicService.searchBlogByTitle(keyword, tags));
	}
}
