package com.culcon.backend.controllers.customer;


import com.culcon.backend.dtos.OTPResponse;
import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import com.culcon.backend.services.OTPService;
import com.culcon.backend.services.OrderService;
import com.culcon.backend.services.UserService;
import com.culcon.backend.services.authenticate.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Nonnull;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Validated
public class CustomerController {

	private final UserService userService;
	private final OrderService orderService;
	private final OTPService otpService;
	private final AuthService authService;


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
		summary = "Get email confirm mail account password")
	@PostMapping("/edit/email/get/otp")
	public ResponseEntity<Object> forgotSendOTP(
		HttpServletRequest request,
		@RequestParam("newEmail")
		@NotEmpty(message = "Email shouldn't be empty")
		@Email
		String newEmail
	) throws MessagingException, UnsupportedEncodingException {
		var account = authService.getUserInformation(request);
		if (account.getEmail().equalsIgnoreCase(newEmail.trim())) {
			throw new IllegalArgumentException("The new email address must be different from the current email address.");
		}

		var otp = otpService.generateOTP(account, newEmail, 14, 7);

		otpService.sendConfirmToNewEmail(otp);
		otpService.sendNoticeToOldEmail(otp);

		return new ResponseEntity<>(OTPResponse.of(otp), HttpStatus.OK);
	}

	@Operation(
		tags = {"Account"},
		summary = "Edit account password")
	@PostMapping("/edit/email")
	public ResponseEntity<Object> editAccountEmailInfo(
		HttpServletRequest request,
		@RequestParam("accountID")
		@NotEmpty(message = "ID shouldn't be empty")
		String accountID,
		@RequestParam("newEmail")
		@NotEmpty(message = "Email shouldn't be empty")
		@Email
		String newEmail,
		@RequestParam("otp")
		@NotEmpty(message = "OTP shouldn't be empty")
		String otp
	) {

		userService.updateCustomerEmail(accountID, newEmail, otp, request);
		return new ResponseEntity<>("Email update successfully", HttpStatus.OK);
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


	@Operation(tags = "Blog")
	@PostMapping("/blog/comment")
	public ResponseEntity<Object> comment(
		HttpServletRequest req,
		@RequestParam String postId,
		@RequestParam String comment
	) {
		return ResponseEntity.ok(userService.commentOnBlog(postId, comment, req));
	}


	@Operation(tags = "Blog")
	@PostMapping("/blog/reply")
	public ResponseEntity<Object> reply(
		HttpServletRequest req,
		@RequestParam String postId,
		@RequestParam String commentId,
		@RequestParam String comment
	) {
		return ResponseEntity.ok(userService.replyComment(postId, commentId, comment, req));
	}


	@Operation(tags = {"Blog", "Comment"})
	@PutMapping("/blog/bookmark")
	public ResponseEntity<Object> bookmark(
		HttpServletRequest req,
		@RequestParam Boolean bookmark,
		@RequestParam String blogId
	) {
		return ResponseEntity.ok(userService.bookmarkBlog(blogId, req, bookmark));
	}

	@Operation(tags = "Blog")
	@GetMapping("/fetch/bookmarked-blog")
	public ResponseEntity<Object> getBookmarkedBlogs(
		HttpServletRequest req
	) {
		return ResponseEntity.ok(userService.getBookmarkedBlog(req));
	}


	@Operation(tags = {"Blog", "Comment"})

	@GetMapping("/fetch/comments")
	public ResponseEntity<Object> getComments(
		HttpServletRequest req
	) {
		return ResponseEntity.ok(userService.getAllComments(req));
	}

	@Operation(tags = {"Blog", "Comment"})
	@DeleteMapping("/comment/deleted")
	public ResponseEntity<Object> deleteComment(
		String commentId,
		HttpServletRequest req
	) {
		return ResponseEntity.ok(userService.deleteComment(commentId, req));
	}
}
