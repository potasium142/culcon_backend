package com.culcon.backend.controllers.customer;


import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import com.culcon.backend.dtos.order.OrderCreation;
import com.culcon.backend.models.user.OrderStatus;
import com.culcon.backend.services.OrderService;
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
	private final OrderService orderService;

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


	@Operation(tags = "Order")
	@PostMapping("/order/create")
	public ResponseEntity<Object> createOrder(
		@RequestBody OrderCreation orderCreation,
		HttpServletRequest request
	) {
		return new ResponseEntity<>(orderService.createOrder(orderCreation, request), HttpStatus.OK);
	}

	@Operation(tags = "Order")
	@GetMapping("/order/fetch/category")
	public ResponseEntity<Object> getOrders(
		@RequestParam OrderStatus orderStatus,
		HttpServletRequest req
	) {
		return ResponseEntity.ok(orderService.getListOfOrder(req, orderStatus));
	}

	@Operation(tags = "Order")
	@GetMapping("/order/fetch/all")
	public ResponseEntity<Object> getAllOrders(
		HttpServletRequest req
	) {
		return ResponseEntity.ok(orderService.getListOfAllOrder(req));
	}


	@Operation(tags = "Order")
	@GetMapping("/order/fetch/detail")
	public ResponseEntity<Object> getOrderDetails(
		HttpServletRequest req,
		@RequestParam Long id
	) {
		return ResponseEntity.ok(orderService.getOrderItem(req, id));
	}


	@Operation(tags = "Order")
	@DeleteMapping("/order/cancel")
	public ResponseEntity<Object> cancelOrder(
		HttpServletRequest req,
		@RequestParam Long id
	) {
		return ResponseEntity.ok(orderService.cancelOrder(req, id));
	}


	@Operation(tags = "Order")
	@PostMapping("/order/update")
	public ResponseEntity<Object> updateOrder(
		HttpServletRequest req,
		@RequestParam Long id,
		@RequestBody OrderCreation orderCreation
	) {
		return ResponseEntity.ok(orderService.updateOrder(req, id, orderCreation));
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
}
