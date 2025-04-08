package com.culcon.backend.services;

import com.culcon.backend.dtos.CartItemDTO;
import com.culcon.backend.dtos.CloudinaryImageDTO;
import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import com.culcon.backend.dtos.blog.BlogComment;
import com.culcon.backend.dtos.blog.UserCommentList;
import com.culcon.backend.exceptions.custom.OTPException;
import com.culcon.backend.models.*;
import com.culcon.backend.repositories.*;
import com.culcon.backend.services.authenticate.AuthService;
import com.culcon.backend.services.authenticate.JwtService;
import com.culcon.backend.services.implement.UserImplement;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	JwtService jwtService;
	@Mock
	AccountRepo accountRepo;

	@Mock
	AuthService authService;

	@Mock
	AuthenticationManager authenticationManager;

	@Mock
	HttpServletRequest request;

	@InjectMocks
	UserImplement userService;

	@Mock
	AccountOTPRepo accountOTPRepo;

	@Mock
	ProductRepo productRepo;

	@Mock
	CloudinaryService cloudinaryService;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	MultipartFile file;

	@Mock
	BlogRepo blogDocRepo;

	@Mock
	PostCommentRepo postCommentRepo;

	@Test
	void userService_getAccounts_Success() {
		List<Account> accounts = Mockito.mock(List.class);

		when(accountRepo.findAll()).thenReturn(accounts);

		List<Account> result = userService.getAccounts();

		Assertions.assertNotNull(result);
	}

	@Test
	void userService_getAccounts_Fail() {

		Assertions.assertTrue(userService.getAccounts().isEmpty());
	}

	@Test
	void userService_getAccountByEmail_Success() throws AccountNotFoundException {
		Account account = Mockito.mock(Account.class);

		when(accountRepo.findAccountByEmail("exsted@email.com")).thenReturn(Optional.of(account));

		Account result = userService.getAccountByEmail("exsted@email.com");

		Assertions.assertNotNull(result);
	}

	@Test
	void userService_getAccountByEmail_fail() {
		Assertions.assertThrows(AccountNotFoundException.class, () -> {
			userService.getAccountByEmail("");
		});
	}

	@Test
	void userService_updateCustomer_Success() {
		Account account = Mockito.mock(Account.class);
		CustomerInfoUpdateRequest newUserData = Mockito.mock(CustomerInfoUpdateRequest.class);

		when(authService.getUserInformation(request))
			.thenReturn(account);

		when(accountRepo.save(account))
			.thenReturn(account);

		Account result = userService.updateCustomer(newUserData, request);

		Assertions.assertNotNull(result);
	}

	@Test
	void userService_updateCustomer_Fail() {
		CustomerInfoUpdateRequest newUserData = Mockito.mock(CustomerInfoUpdateRequest.class);

		Assertions.assertThrows(java.lang.NullPointerException.class, () -> {
			userService.updateCustomer(newUserData, request);
		});
	}

	@Test
	void userService_updateCustomerEmail_Success() {
		// Mock the Account object
		Account account = Mockito.mock(Account.class);

		// Stub method calls
		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getEmail()).thenReturn("oldemail@example.com");

		// Mock OTP validation
		var otp = Mockito.mock(AccountOTP.class);
		when(otp.getOtpExpiration()).thenReturn(Timestamp.valueOf(LocalDateTime.now().plusMinutes(5))); // Not expired
		when(accountOTPRepo.findAccountOTPByOtpAndAccountIdAndEmail("123456", "accountID", "newemail@example.com"))
			.thenReturn(Optional.of(otp));

		// Call the function
		userService.updateCustomerEmail("accountID", "newemail@example.com", "123456", request);

		// Verify interactions
		verify(account).setEmail("newemail@example.com"); // Ensure the email was updated
		verify(accountRepo).save(account); // Ensure account was saved
		verify(accountOTPRepo).delete(otp); // Ensure OTP was deleted
	}

	@Test
	void userService_updateCustomerEmail_Fail_SameEmail() {
		Account account = Mockito.mock(Account.class);
		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getEmail()).thenReturn("same@example.com");

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			userService.updateCustomerEmail("accountID", "same@example.com", "123456", request);
		});

		verify(accountRepo, never()).save(any());
		verify(accountOTPRepo, never()).delete(any());
	}

	@Test
	void userService_updateCustomerEmail_Fail_InvalidOTP() {
		Account account = Mockito.mock(Account.class);
		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getEmail()).thenReturn("old@example.com");

		when(accountOTPRepo.findAccountOTPByOtpAndAccountIdAndEmail("123456", "accountID", "new@example.com"))
			.thenReturn(Optional.empty()); // OTP not found

		Assertions.assertThrows(OTPException.class, () -> {
			userService.updateCustomerEmail("accountID", "new@example.com", "123456", request);
		});

		verify(accountRepo, never()).save(any());
	}

	@Test
	void userService_updateCustomerEmail_Fail_ExpiredOTP() {
		Account account = Mockito.mock(Account.class);
		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getEmail()).thenReturn("old@example.com");

		AccountOTP otp = Mockito.mock(AccountOTP.class);
		when(otp.getOtpExpiration()).thenReturn(Timestamp.valueOf(LocalDateTime.now().minusMinutes(5))); // Expired

		when(accountOTPRepo.findAccountOTPByOtpAndAccountIdAndEmail("123456", "accountID", "new@example.com"))
			.thenReturn(Optional.of(otp));

		Assertions.assertThrows(OTPException.class, () -> {
			userService.updateCustomerEmail("accountID", "new@example.com", "123456", request);
		});

		verify(accountRepo, never()).save(any());
		verify(accountOTPRepo, never()).delete(any());
	}

	@Test
	void userService_updateCustomerPassword_Success() {
		// Mock user and request data
		Account user = Mockito.mock(Account.class);
		when(authService.getUserInformation(request)).thenReturn(user);
		when(user.getPassword()).thenReturn("encodedOldPassword");

		// Mock password match and encoding
		when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
		when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

		// Mock repository save
		when(accountRepo.save(user)).thenReturn(user);
		when(user.getUsername()).thenReturn("user01");

		// Mock authentication response
		AuthenticationResponse authResponse = new AuthenticationResponse("newToken");
		when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(authResponse);

		// Call the function
		AuthenticationResponse result = userService.updateCustomerPassword(
			new CustomerPasswordRequest("oldPassword", "newPassword"), request);

		// Verify that the password was updated and saved
		verify(user).setPassword("encodedNewPassword");
		verify(accountRepo).save(user);
		verify(authService).authenticate(any(AuthenticationRequest.class));

		// Ensure the correct response is returned
		Assertions.assertEquals("newToken", result.accessToken());
	}

	@Test
	void userService_updateCustomerPassword_Fail_WrongOldPassword() {
		Account user = Mockito.mock(Account.class);
		when(authService.getUserInformation(request)).thenReturn(user);
		when(user.getPassword()).thenReturn("encodedOldPassword");

		// Old password does not match
		when(passwordEncoder.matches("wrongOldPassword", "encodedOldPassword")).thenReturn(false);

		Assertions.assertThrows(NoSuchElementException.class, () -> {
			userService.updateCustomerPassword(
				new CustomerPasswordRequest("wrongOldPassword", "newPassword"), request);
		});

		// Verify no update happens
		verify(user, never()).setPassword(anyString());
		verify(accountRepo, never()).save(any());
		verify(authService, never()).authenticate(any());
	}

	@Test
	void userService_updateCustomerPassword_Fail_SaveException() {
		Account user = Mockito.mock(Account.class);
		when(authService.getUserInformation(request)).thenReturn(user);
		when(user.getPassword()).thenReturn("encodedOldPassword");

		when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
		when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

		// Simulate database save failure
		when(accountRepo.save(user)).thenThrow(new RuntimeException("Database error"));

		Assertions.assertThrows(RuntimeException.class, () -> {
			userService.updateCustomerPassword(
				new CustomerPasswordRequest("oldPassword", "newPassword"), request);
		});

		verify(user).setPassword("encodedNewPassword");
		verify(accountRepo).save(user);
		verify(authService, never()).authenticate(any());
	}

	@Test
	void userService_updateCustomerPasswordOTP_Success() {
		// Mock OTP entity and account
		AccountOTP accountOTP = Mockito.mock(AccountOTP.class);
		Account account = Mockito.mock(Account.class);

		when(accountOTPRepo.findByOtpAndAccountId("validOtp", "userId"))
			.thenReturn(Optional.of(accountOTP));
		when(accountOTP.getOtpExpiration())
			.thenReturn(Timestamp.valueOf(LocalDateTime.now().plusMinutes(5))); // OTP still valid
		when(accountOTP.getAccount()).thenReturn(account);
		when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

		// Call the function
		userService.updateCustomerPasswordOTP("validOtp", "userId", "newPassword");

		// Verify password update
		verify(account).setPassword("encodedNewPassword");
		verify(accountRepo).save(account);
		verify(accountOTPRepo).delete(accountOTP);
	}

	@Test
	void userService_updateCustomerPasswordOTP_Fail_InvalidOTP() {
		when(accountOTPRepo.findByOtpAndAccountId("invalidOtp", "userId"))
			.thenReturn(Optional.empty());

		Assertions.assertThrows(OTPException.class, () -> {
			userService.updateCustomerPasswordOTP("invalidOtp", "userId", "newPassword");
		});

		verify(accountRepo, never()).save(any());
		verify(accountOTPRepo, never()).delete(any());
	}

	@Test
	void userService_updateCustomerPasswordOTP_Fail_ExpiredOTP() {
		AccountOTP accountOTP = Mockito.mock(AccountOTP.class);
		when(accountOTPRepo.findByOtpAndAccountId("expiredOtp", "userId"))
			.thenReturn(Optional.of(accountOTP));
		when(accountOTP.getOtpExpiration())
			.thenReturn(Timestamp.valueOf(LocalDateTime.now().minusMinutes(5))); // OTP expired

		Assertions.assertThrows(OTPException.class, () -> {
			userService.updateCustomerPasswordOTP("expiredOtp", "userId", "newPassword");
		});

		verify(accountRepo, never()).save(any());
		verify(accountOTPRepo, never()).delete(any());
	}

	@Test
	void userService_fetchCustomerCart_Success() {
		// Mock user and cart
		Account account = Mockito.mock(Account.class);
		Product product1 = Mockito.mock(Product.class);
		Product product2 = Mockito.mock(Product.class);

		Map<Product, Integer> cart = new HashMap<>();
		cart.put(product1, 2);
		cart.put(product2, 1);

		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getCart()).thenReturn(cart);


		// Call the function
		List<CartItemDTO> result = userService.fetchCustomerCart(request);

		// Verify output
		Assertions.assertNotNull(result);
		Assertions.assertEquals(2, result.size());
		Assertions.assertTrue(result.stream().anyMatch(item -> item.product().equals(product1)));
		Assertions.assertTrue(result.stream().anyMatch(item -> item.product().equals(product2)));
	}

	@Test
	void userService_fetchCustomerCart_EmptyCart() {
		// Mock user with an empty cart
		Account account = Mockito.mock(Account.class);
		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getCart()).thenReturn(Collections.emptyMap());

		// Call the function
		List<CartItemDTO> result = userService.fetchCustomerCart(request);

		// Verify output
		Assertions.assertNotNull(result);
		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	void userService_fetchCustomerCart_Fail_NullCart() {
		// Mock user with a null cart
		Account account = Mockito.mock(Account.class);
		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getCart()).thenReturn(null);

		// Call the function
		Assertions.assertThrows(NullPointerException.class, () -> {
			userService.fetchCustomerCart(request);
		});
	}


	@Test
	void userService_addProductToCart_Success() {
		// Mock dependencies
		Account account = Mockito.mock(Account.class);
		Product product = Mockito.mock(Product.class);

		String productId = "product123";
		int amountToAdd = 2;

		Map<Product, Integer> cart = new HashMap<>();
		cart.put(product, 1); // Initial cart contains 1 item of this product

		when(productRepo.findById(productId)).thenReturn(Optional.of(product));
		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getCart()).thenReturn(cart);
		when(product.getAvailableQuantity()).thenReturn(10);

		// Call function
		CartItemDTO result = userService.addProductToCart(productId, amountToAdd, request);

		// Verify
		Assertions.assertNotNull(result);
		Assertions.assertEquals(3, result.amount());
		Assertions.assertEquals(product, result.product());

		verify(accountRepo).save(account);
	}

	@Test
	void userService_addProductToCart_Fail_ProductNotFound() {
		String productId = "nonExistentProduct";
		when(productRepo.findById(productId)).thenReturn(Optional.empty());

		Assertions.assertThrows(NoSuchElementException.class, () -> {
			userService.addProductToCart(productId, 1, request);
		});
	}

	@Test
	void userService_addProductToCart_Fail_InsufficientQuantity() {
		// Mock dependencies
		Account account = Mockito.mock(Account.class);
		Product product = Mockito.mock(Product.class);

		String productId = "product123";
		int amountToAdd = 10; // Exceeding available quantity

		Map<Product, Integer> cart = new HashMap<>();
		cart.put(product, 5); // Already has 5 items

		when(productRepo.findById(productId)).thenReturn(Optional.of(product));
		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getCart()).thenReturn(cart);
		when(product.getAvailableQuantity()).thenReturn(12); // Only 12 available

		Assertions.assertThrows(RuntimeException.class, () -> {
			userService.addProductToCart(productId, amountToAdd, request);
		});
	}

	@Test
	void userService_addProductToCart_RemoveProductWhenAmountIsZero() {
		// Mock dependencies
		Account account = Mockito.mock(Account.class);
		Product product = Mockito.mock(Product.class);

		String productId = "product123";
		int amountToAdd = -1; // Removing item

		Map<Product, Integer> cart = new HashMap<>();
		cart.put(product, 1); // Currently has 1 item

		when(productRepo.findById(productId)).thenReturn(Optional.of(product));
		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getCart()).thenReturn(cart);

		// Call function
		CartItemDTO result = userService.addProductToCart(productId, amountToAdd, request);

		// Verify item was removed
		Assertions.assertNotNull(result);
		Assertions.assertEquals(0, result.amount());
	}

	@Test
	void userService_setProductAmountInCart_Success() {
		// Mock dependencies
		Account account = Mockito.mock(Account.class);
		Product product = Mockito.mock(Product.class);

		String productId = "product123";
		int newAmount = 3;

		Map<Product, Integer> cart = new HashMap<>();
		cart.put(product, 1); // Initial cart has 1 item

		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getCart()).thenReturn(cart);
		when(product.getId()).thenReturn(productId);
		when(product.getAvailableQuantity()).thenReturn(10);

		// Call function
		Map<String, Object> result = userService.setProductAmountInCart(productId, newAmount, request);

		// Verify
		Assertions.assertNotNull(result);
		Assertions.assertEquals(productId, result.get("productId"));
		Assertions.assertEquals(newAmount, result.get("amount"));

		verify(accountRepo).save(account);
	}

	@Test
	void userService_setProductAmountInCart_Fail_ProductNotFound() {
		// Mock dependencies
		Account account = Mockito.mock(Account.class);
		Map<Product, Integer> cart = new HashMap<>(); // Empty cart

		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getCart()).thenReturn(cart);

		String productId = "nonExistentProduct";

		Assertions.assertThrows(NoSuchElementException.class, () -> {
			userService.setProductAmountInCart(productId, 2, request);
		});
	}

	@Test
	void userService_setProductAmountInCart_Fail_InsufficientQuantity() {
		// Mock dependencies
		Account account = Mockito.mock(Account.class);
		Product product = Mockito.mock(Product.class);

		String productId = "product123";
		int newAmount = 15; // Exceeds available quantity

		Map<Product, Integer> cart = new HashMap<>();
		cart.put(product, 1); // Currently has 1 item

		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getCart()).thenReturn(cart);
		when(product.getId()).thenReturn(productId);
		when(product.getAvailableQuantity()).thenReturn(10); // Only 10 available

		Assertions.assertThrows(RuntimeException.class, () -> {
			userService.setProductAmountInCart(productId, newAmount, request);
		});
	}

	@Test
	void userService_setProductAmountInCart_RemoveProductWhenAmountIsZero() {
		// Mock dependencies
		Account account = Mockito.mock(Account.class);
		Product product = Mockito.mock(Product.class);

		String productId = "product123";
		int newAmount = 0; // Removing item

		Map<Product, Integer> cart = new HashMap<>();
		cart.put(product, 1); // Currently has 1 item

		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getCart()).thenReturn(cart);
		when(product.getId()).thenReturn(productId);

		// Call function
		Map<String, Object> result = userService.setProductAmountInCart(productId, newAmount, request);

		// Verify item was removed
		Assertions.assertNotNull(result);
		Assertions.assertEquals(productId, result.get("productId"));
		Assertions.assertEquals(newAmount, result.get("amount"));
	}

	@Test
	void userService_removeProductFromCart_Success() {
		// Mock dependencies
		Account account = Mockito.mock(Account.class);
		Product product = Mockito.mock(Product.class);

		String productId = "product123";

		Map<Product, Integer> cart = new HashMap<>();
		cart.put(product, 2); // Cart initially has the product

		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getCart()).thenReturn(cart);
		when(product.getId()).thenReturn(productId);
		when(accountRepo.save(account)).thenReturn(account);

		// Call function
		Boolean result = userService.removeProductFromCart(productId, request);

		// Verify
		Assertions.assertTrue(result);
	}

	@Test
	void userService_removeProductFromCart_Fail_ProductNotFound() {
		// Mock dependencies
		Account account = Mockito.mock(Account.class);
		Map<Product, Integer> cart = new HashMap<>(); // Empty cart

		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getCart()).thenReturn(cart);

		String productId = "nonExistentProduct";

		Assertions.assertThrows(NoSuchElementException.class, () -> {
			userService.removeProductFromCart(productId, request);
		});
	}

	@Test
	void userService_updateUserProfilePicture_Success() throws IOException {
		// Mock dependencies
		Account account = Mockito.mock(Account.class);
		Map<String, Object> uploadInfo = Map.of("url", "https://cloudinary.com/sample_image.jpg");

		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getId()).thenReturn("user123");
		when(cloudinaryService.uploadImage(eq(file), anyMap())).thenReturn(uploadInfo);

		// Call function
		CloudinaryImageDTO result = userService.updateUserProfilePicture(file, request);

		// Verify interactions
		verify(cloudinaryService).uploadImage(eq(file), anyMap());
		verify(account).setProfilePictureUri("https://cloudinary.com/sample_image.jpg");

		// Assertions
		Assertions.assertEquals("https://cloudinary.com/sample_image.jpg", result.url());
	}

	@Test
	void userService_updateUserProfilePicture_Fail_UploadThrowsException() throws IOException {
		// Mock dependencies
		Account account = Mockito.mock(Account.class);

		when(authService.getUserInformation(request)).thenReturn(account);
		when(account.getId()).thenReturn("user123");
		when(cloudinaryService.uploadImage(eq(file), anyMap())).thenThrow(new IOException("Upload failed"));

		// Expect exception
		Assertions.assertThrows(IOException.class, () -> {
			userService.updateUserProfilePicture(file, request);
		});

		// Verify cloudinary upload attempt
		verify(cloudinaryService).uploadImage(eq(file), anyMap());

		// Ensure profile picture was not updated
		verify(account, never()).setProfilePictureUri(anyString());
	}

	@Test
	void userService_commentOnBlog_Success() {
		// Mock dependencies
		Account account = Mockito.mock(Account.class);
		Blog blogPost = Mockito.mock(Blog.class);
		PostComment postComment = Mockito.mock(PostComment.class);

		String blogId = "blog123";
		String commentText = "Great blog!";

		when(authService.getUserInformation(request)).thenReturn(account);
		when(blogDocRepo.findById(blogId)).thenReturn(Optional.of(blogPost));
		when(postCommentRepo.save(any(PostComment.class))).thenReturn(postComment);

		// Ensure postComment.getAccount() is not null
		when(postComment.getAccount()).thenReturn(account);
		when(postComment.getComment()).thenReturn(commentText);
		when(postComment.getId()).thenReturn("comment123");
		when(postComment.getStatus()).thenReturn(CommentStatus.NORMAL); // Return a boolean, not a BlogComment

		Instant nowInstant = Instant.parse("2024-01-01T12:00:00Z");
		Timestamp fixedTimestamp = Timestamp.from(nowInstant);
		when(postComment.getTimestamp()).thenReturn(fixedTimestamp);
		when(account.getUsername()).thenReturn("TestUser");
		when(account.getProfilePictureUri()).thenReturn("profile.jpg");

		// Call function
		BlogComment result = userService.commentOnBlog(blogId, commentText, request);

		// Verify interactions
		verify(authService).getUserInformation(request);
		verify(blogDocRepo).findById(blogId);
		verify(postCommentRepo).save(any(PostComment.class));

		// Assertions
		Assertions.assertNotNull(result);
		Assertions.assertEquals("comment123", result.id());
		Assertions.assertEquals(commentText, result.comment());
		Assertions.assertEquals("TestUser", result.accountName());
		Assertions.assertEquals("profile.jpg", result.profilePicture());
	}


	@Test
	void userService_commentOnBlog_Fail_BlogNotFound() {
		// Arrange - Mock dependencies
		String blogId = "blog123";
		String commentText = "Great blog!";

		when(blogDocRepo.findById(blogId)).thenReturn(Optional.empty());

		// Act & Assert - Expect exception with message validation
		NoSuchElementException exception = Assertions.assertThrows(
			NoSuchElementException.class,
			() -> userService.commentOnBlog(blogId, commentText, request)
		);

		Assertions.assertEquals("Blog not found", exception.getMessage());

		// Verify interactions
		verify(blogDocRepo).findById(blogId);
		verify(postCommentRepo, never()).save(any(PostComment.class));
	}

	@Test
	void userService_replyComment_Success() {
		// Arrange - Mock dependencies
		String blogId = "blog123";
		String commentId = "comment456";
		String replyText = "I agree with your point!";

		Account account = Mockito.mock(Account.class);
		Blog blogPost = Mockito.mock(Blog.class);
		PostComment parentComment = Mockito.mock(PostComment.class);

		// Create an actual reply comment instead of mocking it
		PostComment replyComment = new PostComment();
		replyComment.setId("reply789");
		replyComment.setComment(replyText);
		replyComment.setAccount(account);
		replyComment.setTimestamp(Timestamp.from(Instant.parse("2024-01-01T12:00:00Z")));

		// Mock valid Account details
		when(account.getUsername()).thenReturn("testUser");
		when(account.getProfilePictureUri()).thenReturn("http://example.com/profile.jpg");

		when(authService.getUserInformation(request)).thenReturn(account);
		when(blogDocRepo.existsById(blogId)).thenReturn(true);
		when(blogDocRepo.findById(blogId)).thenReturn(Optional.of(blogPost));
		when(postCommentRepo.findById(commentId)).thenReturn(Optional.of(parentComment));

		when(postCommentRepo.save(any(PostComment.class))).thenReturn(replyComment);

		// Expected result (directly calling from instead of mocking)
		BlogComment expectedBlogComment = BlogComment.from(replyComment);

		// Act
		BlogComment result = userService.replyComment(blogId, commentId, replyText, request);

		// Assert & Verify
		verify(authService).getUserInformation(request);
		verify(blogDocRepo).existsById(blogId);
		verify(blogDocRepo).findById(blogId);
		verify(postCommentRepo).findById(commentId);
		verify(postCommentRepo).save(any(PostComment.class));

		Assertions.assertNotNull(result);
		Assertions.assertEquals(expectedBlogComment, result);
	}


	@Test
	void userService_replyComment_Fail_BlogNotFound() {
		// Arrange
		String blogId = "blog123";
		String commentId = "comment456";
		String replyText = "I agree with your point!";

		when(blogDocRepo.existsById(blogId)).thenReturn(false);

		// Act & Assert
		NoSuchElementException exception = Assertions.assertThrows(
			NoSuchElementException.class,
			() -> userService.replyComment(blogId, commentId, replyText, request)
		);

		Assertions.assertEquals("Blog not found", exception.getMessage());

		verify(blogDocRepo).existsById(blogId);
		verify(blogDocRepo, never()).findById(blogId);
		verify(postCommentRepo, never()).findById(commentId);
		verify(postCommentRepo, never()).save(any(PostComment.class));
	}

	@Test
	void userService_replyComment_Fail_CommentNotFound() {
		// Arrange
		String blogId = "blog123";
		String commentId = "comment456";
		String replyText = "I agree with your point!";

		when(blogDocRepo.existsById(blogId)).thenReturn(true);
		when(blogDocRepo.findById(blogId)).thenReturn(Optional.of(Mockito.mock(Blog.class)));
		when(postCommentRepo.findById(commentId)).thenReturn(Optional.empty());

		// Act & Assert
		NoSuchElementException exception = Assertions.assertThrows(
			NoSuchElementException.class,
			() -> userService.replyComment(blogId, commentId, replyText, request)
		);

		Assertions.assertEquals("Comment not found", exception.getMessage());

		verify(blogDocRepo).existsById(blogId);
		verify(blogDocRepo).findById(blogId);
		verify(postCommentRepo).findById(commentId);
		verify(postCommentRepo, never()).save(any(PostComment.class));
	}

	@Test
	void userService_getAllComments_Success() {

		Account account = Mockito.mock(Account.class);
		List<PostComment> comments = Mockito.mock(List.class);

		when(authService.getUserInformation(request)).thenReturn(account);
		when(postCommentRepo.findAllByAccount(account)).thenReturn(comments);

		List<UserCommentList> result = userService.getAllComments(request);


		Assertions.assertNotNull(result);

	}

//	@Test
//	void userService_bookmarkBlog_Success_AddBookmark() {
//		// Arrange - Mock dependencies
//		String blogId = "blog123";
//		Account account = Mockito.mock(Account.class);
//		Set<String> bookmarkedPosts = new HashSet<>();
//
//		when(authService.getUserInformation(request)).thenReturn(account);
//		when(blogDocRepo.existsById(blogId)).thenReturn(true);
//		when(account.getBookmarkedPost()).thenReturn(bookmarkedPosts);
//		when(accountRepo.save(account)).thenReturn(account);
//
//		// Act
//		Boolean result = userService.bookmarkBlog(blogId, request, true);
//
//		// Assert & Verify
//		verify(authService).getUserInformation(request);
//		verify(blogDocRepo).existsById(blogId);
//		verify(accountRepo).save(account);
//
//		Assertions.assertTrue(result);
//	}

//	@Test
//	void userService_bookmarkBlog_Success_RemoveBookmark() {
//		// Arrange - Mock dependencies
//		String blogId = "blog123";
//		Account account = Mockito.mock(Account.class);
//		Set<String> bookmarkedPosts = new HashSet<>(Set.of(blogId));
//
//		when(authService.getUserInformation(request)).thenReturn(account);
//		when(blogDocRepo.existsById(blogId)).thenReturn(true);
//		when(account.getBookmarkedPost()).thenReturn(bookmarkedPosts);
//		when(accountRepo.save(account)).thenReturn(account);
//
//		// Act
//		Boolean result = userService.bookmarkBlog(blogId, request, false);
//
//		// Assert & Verify
//		verify(authService).getUserInformation(request);
//		verify(blogDocRepo).existsById(blogId);
//		verify(accountRepo).save(account);
//
//		Assertions.assertTrue(result);
//	}

//	@Test
//	void userService_bookmarkBlog_Fail_BlogNotFound() {
//		// Arrange - Mock dependencies
//		String blogId = "blog123";
//
//		when(authService.getUserInformation(request)).thenReturn(Mockito.mock(Account.class));
//		when(blogDocRepo.existsById(blogId)).thenReturn(false);
//
//		// Act & Assert
//		Assertions.assertThrows(NoSuchElementException.class,
//			() -> userService.bookmarkBlog(blogId, request, true));
//
//		verify(blogDocRepo).existsById(blogId);
//	}

//	@Test
//	void userService_getBookmarkedBlog_Success() {
//		// Arrange - Mock dependencies
//		Account account = Mockito.mock(Account.class);
//		List<String> bookmarkedPostIds = List.of("blog123", "blog456");
//
//		Blog blog1 = Blog.builder().id("blog123").title("Title 1").thumbnail("thumb1.jpg").build();
//		Blog blog2 = Blog.builder().id("blog456").title("Title 2").thumbnail("thumb2.jpg").build();
//
//		when(authService.getUserInformation(request)).thenReturn(account);
//		when(account.getBookmarkedPost()).thenReturn(new HashSet<>(bookmarkedPostIds));
//
//		when(blogDocRepo.findById("blog123")).thenReturn(Optional.of(blog1));
//		when(blogDocRepo.findById("blog456")).thenReturn(Optional.of(blog2));
//
//		// Act
//		List<BlogItemInList> result = userService.getBookmarkedBlog(request);
//
//		// Assert & Verify
//		verify(authService).getUserInformation(request);
//		verify(account).getBookmarkedPost();
//		verify(blogDocRepo).findById("blog123");
//		verify(blogDocRepo).findById("blog456");
//
//		Assertions.assertEquals(2, result.size());
//		Assertions.assertEquals("Title 1", result.get(0).title());
//		Assertions.assertEquals("thumb1.jpg", result.get(0).imageUrl());
//		Assertions.assertEquals("Title 2", result.get(1).title());
//		Assertions.assertEquals("thumb2.jpg", result.get(1).imageUrl());
//	}


//	@Test
//	void userService_getBookmarkedBlog_Success_WithMissingBlog() {
//		// Arrange - Mock dependencies
//		Account account = Mockito.mock(Account.class);
//		List<String> bookmarkedPostIds = List.of("blog123", "blog999");
//
//		Blog blog1 = Blog.builder().id("blog123").title("Title 1").thumbnail("thumb1.jpg").build();
//		Blog emptyBlog = Blog.builder().build(); // Default empty blog
//
//		when(authService.getUserInformation(request)).thenReturn(account);
//		when(account.getBookmarkedPost()).thenReturn(new HashSet<>(bookmarkedPostIds));
//
//		when(blogDocRepo.findById("blog123")).thenReturn(Optional.of(blog1));
//		when(blogDocRepo.findById("blog999")).thenReturn(Optional.empty());
//
//		// Act
//		List<BlogItemInList> result = userService.getBookmarkedBlog(request);
//
//		// Assert & Verify
//		verify(authService).getUserInformation(request);
//		verify(account).getBookmarkedPost();
//		verify(blogDocRepo).findById("blog123");
//		verify(blogDocRepo).findById("blog999");
//
//		Assertions.assertEquals(2, result.size());
//		Assertions.assertEquals("Title 1", result.get(0).title());
//		Assertions.assertEquals("thumb1.jpg", result.get(0).imageUrl());
//
//		// Ensuring the second item is the fallback empty blog
//		Assertions.assertNull(result.get(1).title()); // Assuming `Blog.builder().build()` creates null fields
//	}


//	@Test
//	void userService_getBookmarkedBlog_EmptyBookmarks() {
//		// Arrange - Mock dependencies
//		Account account = Mockito.mock(Account.class);
//
//		when(authService.getUserInformation(request)).thenReturn(account);
//		when(account.getBookmarkedPost()).thenReturn(Collections.emptySet());
//
//		// Act
//		List<BlogItemInList> result = userService.getBookmarkedBlog(request);
//
//		// Assert & Verify
//		verify(authService).getUserInformation(request);
//		verify(account).getBookmarkedPost();
//		verifyNoInteractions(blogDocRepo); // No DB calls should be made
//
//		Assertions.assertTrue(result.isEmpty());
//	}

	@Test
	void userService_deleteComment_Success() {
		// Arrange - Mock dependencies
		Account account = Mockito.mock(Account.class);
		PostComment comment = Mockito.spy(PostComment.builder().id("comment123").build());

		when(authService.getUserInformation(request)).thenReturn(account);
		when(postCommentRepo.findByIdAndAccount("comment123", account))
			.thenReturn(Optional.of(comment));
		when(postCommentRepo.save(any(PostComment.class))).thenReturn(comment);

		// Act
		Boolean result = userService.deleteComment("comment123", request);

		// Assert & Verify
		verify(authService).getUserInformation(request);
		verify(postCommentRepo).findByIdAndAccount("comment123", account);
		verify(postCommentRepo).save(comment);

		Assertions.assertTrue(result);
		Assertions.assertTrue(comment.getStatus() == CommentStatus.DELETED);
	}


	@Test
	void userService_deleteComment_CommentNotFound() {
		// Arrange
		Account account = Mockito.mock(Account.class);

		when(authService.getUserInformation(request)).thenReturn(account);
		when(postCommentRepo.findByIdAndAccount("comment999", account))
			.thenReturn(Optional.empty());

		// Act & Assert
		NoSuchElementException exception = Assertions.assertThrows(
			NoSuchElementException.class,
			() -> userService.deleteComment("comment999", request)
		);

		Assertions.assertEquals("Comment not found", exception.getMessage());
		verify(authService).getUserInformation(request);
		verify(postCommentRepo).findByIdAndAccount("comment999", account);
	}
}
