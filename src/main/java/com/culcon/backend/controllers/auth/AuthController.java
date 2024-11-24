package com.culcon.backend.controllers.auth;

import com.culcon.backend.configs.LogoutService;
import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;
import com.culcon.backend.services.authenticate.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

	private final AuthService authService;
	private final LogoutService logoutService;

	@Operation(
		tags = {"Authentication", "Account"},
		summary = "Customer register API")
	@PostMapping("/register")
	public ResponseEntity<Object> registerCustomer(
		@Valid
		@RequestBody
		CustomerRegisterRequest request) {
		var registerLoginToken = authService.registerCustomer(request);
		return new ResponseEntity<>(
			registerLoginToken,
			HttpStatus.OK);
	}

	@Operation(tags = {"Authentication"})
	@PostMapping("/signin")
	public ResponseEntity<Object> signIn(
		@RequestBody
		@Valid
		AuthenticationRequest authenticate) {
		var authenStatus = authService.authenticate(authenticate);
		return new ResponseEntity<>(authenStatus, HttpStatus.OK);
	}

	@Operation(tags = {"Authentication"})
	@PostMapping("/logout")
	public void logout(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) {
		logoutService.logout(request, response, authentication);
	}

	@Operation(
		tags = {"Account"},
		summary = "Get raw account information")
	@GetMapping("/account")
	public ResponseEntity<Object> getCurrentLoginUser(
		HttpServletRequest request) {
		return authService.getUserInformation(request);
	}

	@Operation(
			tags = {"Account"},
			summary = "Edit account information")
	@PostMapping("/account/edit")
	public ResponseEntity<Object> editAccountInfo(
			HttpServletRequest request, @Valid @RequestBody CustomerInfoUpdateRequest newUserData

//			HttpServletResponse response, Authentication authentication
			) {
		ResponseEntity<Object> updateResponse = authService.updateCustomer(newUserData, request);
		return updateResponse;
//		logoutService.logout(request, response, authentication);
	}


	@Operation(
			tags = {"Account"},
			summary = "Edit account password")
	@PostMapping("/account/password/edit")
	public ResponseEntity<Object> editAccountPasswordInfo(
			HttpServletRequest request, @Valid @RequestBody CustomerPasswordRequest newUserData

//			HttpServletResponse response, Authentication authentication
	) {
		ResponseEntity<Object> updateResponse = authService.updateCustomerPassword(newUserData, request);
		return updateResponse;
//		logoutService.logout(request, response, authentication);
	}
}
