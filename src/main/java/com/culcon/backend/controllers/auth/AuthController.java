package com.culcon.backend.controllers.auth;

import com.culcon.backend.configs.LogoutService;
import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;
import com.culcon.backend.models.Account;
import com.culcon.backend.services.OTPService;
import com.culcon.backend.services.UserService;
import com.culcon.backend.services.authenticate.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

	private final AuthService authService;
	private final LogoutService logoutService;

	private final UserService userService;
	private final OTPService otpService;

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


	@Operation(tags = {"Authentication"})
	@PostMapping("/forgot/otp/get")
	public ResponseEntity<Object> forgotSendOTP(
			@RequestParam("email")
			@NotEmpty(message = "Email shouldn't be empty")
			@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$", message = "Invalid email format")
			String email
			) throws MessagingException, UnsupportedEncodingException, AccountNotFoundException {
		Account account = userService.getAccountByEmail(email);

		otpService.generateOneTimePassword(account);

		return new ResponseEntity<>("Check your email for OTP code", HttpStatus.OK);
	}

	@Operation(tags = {"Authentication"})
	@PostMapping("/forgot/reset")
	public ResponseEntity<Object> forgotResetPassword(
			@RequestParam("email")
			@NotEmpty(message = "Email shouldn't be empty")
			@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$", message = "Invalid email format")
			String email,
			@RequestParam("newpassword")
			@NotEmpty(message = "New password shouldn't be empty")
			String password,
			@RequestParam("otp") String otp
	) throws  AccountNotFoundException {
		Account account = userService.getAccountByEmail(email);

		if (otpService.getAccountOTPById(account.getId()) != null){

			if (otpService.compareOTPs(otp,otpService.getAccountOTPById(account.getId()).getOtp()) ) {
				userService.updateCustomerPasswordOTP(password, account);
			}
			else{
				return new ResponseEntity<>("OTP is invalid", HttpStatus.BAD_REQUEST);
			}
		}
		else {
			return new ResponseEntity<>("Account have no OTP, please generate it first", HttpStatus.BAD_REQUEST);
		}
		otpService.clearOTP(otpService.getAccountOTPById(account.getId()));
		return new ResponseEntity<>("Password update successfully", HttpStatus.OK);
	}


	@Operation(
		tags = {"Account"},
		summary = "Get raw account information")
	@GetMapping("/account")
	public ResponseEntity<Object> getCurrentLoginUser(
		HttpServletRequest request) {
		var user = authService.getUserInformation(request);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}




}
