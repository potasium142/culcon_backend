package com.culcon.backend.controllers.customer;


import com.culcon.backend.services.CartService;
import com.culcon.backend.services.authenticate.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Validated
public class CustomerController {

	private final CartService cartService;
	private final AuthService authService;

	@Operation(tags = "Permission Test", summary = "Test permission for guest")
	@GetMapping("/test_permission")
	public String permissionTest() {
		return "sucess";
	}


	@Operation(tags = "Permission Test", summary = "Get customer cart")
	@GetMapping("/cart")
	public ResponseEntity<Object> getCustomerCart(HttpServletRequest request) {
		return cartService.getUserCartItems(request);
	}
}
