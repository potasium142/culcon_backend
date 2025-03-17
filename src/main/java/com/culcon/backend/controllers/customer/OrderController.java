package com.culcon.backend.controllers.customer;

import com.culcon.backend.dtos.order.OrderCreation;
import com.culcon.backend.dtos.order.OrderUpdate;
import com.culcon.backend.models.OrderStatus;
import com.culcon.backend.models.PaymentMethod;
import com.culcon.backend.services.OrderService;
import com.paypal.sdk.exceptions.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/customer/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@Operation(tags = "Order")
	@PostMapping("/create")
	public ResponseEntity<Object> createOrder(
		@Valid @RequestBody OrderCreation orderCreation,
		HttpServletRequest request
	) throws IOException, ApiException {
		return new ResponseEntity<>(orderService.createOrder(orderCreation, request), HttpStatus.OK);
	}

	@Operation(tags = "Order")
	@GetMapping("/fetch/category")
	public ResponseEntity<Object> getOrders(
		@RequestParam OrderStatus orderStatus,
		HttpServletRequest req
	) {
		return ResponseEntity.ok(orderService.getListOfOrderByStatus(req, orderStatus));
	}

	@Operation(tags = "Order")
	@GetMapping("/fetch/all")
	public ResponseEntity<Object> getAllOrders(
		HttpServletRequest req
	) {
		return ResponseEntity.ok(orderService.getListOfAllOrder(req));
	}


	@Operation(tags = "Order")
	@GetMapping("/fetch/detail")
	public ResponseEntity<Object> getOrderDetails(
		HttpServletRequest req,
		@RequestParam String id
	) {
		return ResponseEntity.ok(orderService.getOrderDetail(req, id));
	}

	@Operation(tags = "Order")
	@PostMapping("/receive")
	public ResponseEntity<Object> receiveOrder(
		HttpServletRequest req,
		@RequestParam String id
	) {
		return ResponseEntity.ok(orderService.receiveOrder(req, id));
	}


	@Operation(tags = "Order")
	@DeleteMapping("/cancel")
	public ResponseEntity<Object> cancelOrder(
		HttpServletRequest req,
		@RequestParam String id
	) {
		return ResponseEntity.ok(orderService.cancelOrder(req, id));
	}


	@Operation(tags = "Order")
	@PatchMapping("/update/info")
	public ResponseEntity<Object> updateOrder(
		HttpServletRequest req,
		@RequestParam String id,
		@Valid @RequestBody OrderUpdate orderCreation
	) {
		return ResponseEntity.ok(orderService.updateOrder(req, id, orderCreation));
	}

	@Deprecated
	@Operation(tags = "Order")
	@PatchMapping("/update/payment")
	public ResponseEntity<Object> updatePayment(
		HttpServletRequest req,
		@RequestParam String id,
		@RequestParam PaymentMethod paymentMethod
	) throws IOException, ApiException {
		return ResponseEntity.ok(orderService.changePayment(req, id, paymentMethod));
	}

	@Deprecated
	@Operation(tags = "Order")
	@PatchMapping("/update/coupon")
	public ResponseEntity<Object> updateCoupon(
		HttpServletRequest req,
		@RequestParam String id,
		@RequestParam String couponId
	) throws IOException, ApiException {
		return ResponseEntity.ok(orderService.updateOrderCoupon(req, id, couponId));
	}
}
