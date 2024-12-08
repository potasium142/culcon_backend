package com.culcon.backend.controllers.auth;

import com.culcon.backend.configs.LogoutService;
import com.culcon.backend.dtos.OTPResetPassword;
import com.culcon.backend.dtos.OTPResponse;
import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;
import com.culcon.backend.models.user.Account;
import com.culcon.backend.services.OTPService;
import com.culcon.backend.services.UserService;
import com.culcon.backend.services.authenticate.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.security.auth.login.AccountNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

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
		@Email
		String email
	) throws MessagingException, UnsupportedEncodingException, AccountNotFoundException {
		Account account = userService.getAccountByEmail(email);

		var otp = otpService.generateOTP(account, 14, 7);

		otpService.sendOTPEmail(otp);

		return new ResponseEntity<>(OTPResponse.of(otp), HttpStatus.OK);
	}

	@Operation(tags = {"Authentication"})
	@PostMapping("/forgot/reset")
	public ResponseEntity<Object> forgotResetPassword(
		@Valid @RequestBody OTPResetPassword otpForm
	) {
		userService.updateCustomerPasswordOTP(otpForm.otp(), otpForm.id(), otpForm.password());
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

	@Operation(
		tags = {"Authentication"},
		summary = "Signin with google")
	@GetMapping("/signin/google")
	public RedirectView googleSignIn() {
		return new RedirectView("/oauth2/authorization/google");
	}


	@Operation(
			tags = {"Authentication"},
			summary = "Signin with google completed")
	@GetMapping("/signin/google/done")
	public ResponseEntity<Object> googleSignInDone(@RequestParam(required = false) String token) {
		// Check if the token is missing
		if (token == null || token.isEmpty()) {
			// Return a JSON response with error message
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", "There's no account linked to the service, please create an account with the email.");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

		// If token is present, process it and return a success message
		Map<String, String> successResponse = new HashMap<>();
		successResponse.put("message", "Successfully signed in with Google.");
		successResponse.put("token", token);

		return new ResponseEntity<>(successResponse, HttpStatus.OK);
	}




	@Operation(
		tags = {"Account"},
		summary = "Get raw account information")
	@GetMapping("/account/all/test")
	public ResponseEntity<Object> getAllCustomer() {

		return new ResponseEntity<>(userService.getAccounts(), HttpStatus.OK);
	}
}
