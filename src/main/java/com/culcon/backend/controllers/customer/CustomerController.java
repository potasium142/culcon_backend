package com.culcon.backend.controllers.customer;


import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import com.culcon.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Validated
public class CustomerController {

	private final UserService userService;

	@Operation(tags = "Permission Test", summary = "Test permission for guest")
	@GetMapping("/test_permission")
	public String permissionTest() {
		return "sucess";
	}


	@Operation(tags = "Cart", summary = "Get customer cart")
	@GetMapping("/cart/fetch")
	public ResponseEntity<Object> getCustomerCart(HttpServletRequest request) {
		return new ResponseEntity<>(userService.fetchCustomerCart(request), HttpStatus.OK);
	}

	@Operation(tags = "Cart", summary = "Remove product from cart")
	@DeleteMapping("/cart/remove")
	public ResponseEntity<Object> removeProductFromCart(HttpServletRequest request, String id) {
		return new ResponseEntity<>(userService.removeProductFromCart(id, request), HttpStatus.OK);
	}


	@Operation(tags = "Cart", summary = "Set product amount in cart")
	@PutMapping("/cart/set")
	public ResponseEntity<Object> setProductAmountInCart(
		HttpServletRequest request,
		@Nonnull
		@RequestParam String id,
		@Nonnull
		@RequestParam Integer quantity) {
		return new ResponseEntity<>(userService.setProductAmountInCart(id, quantity, request), HttpStatus.OK);
	}

	@Operation(tags = "Cart", summary = "Put product to cart")
	@PutMapping("/cart/add")
	public ResponseEntity<Object> addProductToCart(
		HttpServletRequest request,
		@Nonnull
		@RequestParam String id,
		@Nonnull
		@RequestParam Integer quantity) {
		return new ResponseEntity<>(userService.addProductToCart(id, quantity, request), HttpStatus.OK);
	}

	@Operation(
		tags = {"Account"},
		summary = "Edit account information")
	@PostMapping("/edit/profile")
	public ResponseEntity<Object> editAccountInfo(
		HttpServletRequest request,
		@Valid @RequestBody CustomerInfoUpdateRequest newUserData) {
		var updateResponse = userService.updateCustomer(newUserData, request);
		return new ResponseEntity<>(updateResponse, HttpStatus.OK);
	}


	@Operation(
		tags = {"Account"},
		summary = "Edit account password")
	@PostMapping("/edit/password")
	public ResponseEntity<Object> editAccountPasswordInfo(
		HttpServletRequest request,
		@Valid @RequestBody CustomerPasswordRequest newUserData) {
		var updateResponse = userService.updateCustomerPassword(newUserData, request);
		return new ResponseEntity<>(updateResponse, HttpStatus.OK);
	}


	@Operation(
		tags = {"Account"},
		summary = "Edit account profile picture")
	@PostMapping(value = "/edit/profile/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> editUserProfilePicture(
		HttpServletRequest request,
		@Valid @RequestPart MultipartFile file
	) throws IOException {
		return ResponseEntity.ok(userService.updateUserProfilePicture(file, request));
	}
}
