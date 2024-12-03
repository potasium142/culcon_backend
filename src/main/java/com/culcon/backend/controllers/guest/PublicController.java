package com.culcon.backend.controllers.guest;

import com.culcon.backend.services.PublicService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
