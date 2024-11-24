package com.culcon.backend.controllers.customer;


import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import com.culcon.backend.services.CartService;
import com.culcon.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Validated
public class CustomerController {

	private final CartService cartService;
	private final UserService userService;

	@Operation(tags = "Permission Test", summary = "Test permission for guest")
	@GetMapping("/test_permission")
	public String permissionTest() {
		return "sucess";
	}


	@Operation(tags = "Cart", summary = "Get customer cart")
	@GetMapping("/cart")
	public ResponseEntity<Object> getCustomerCart(HttpServletRequest request) {
		return new ResponseEntity<>("Not yet implemented", HttpStatus.NOT_IMPLEMENTED);
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
}
