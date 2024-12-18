package com.culcon.backend.controllers.customer;


import com.culcon.backend.services.PaymentService;
import com.paypal.sdk.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@GetMapping("/get")
	public ResponseEntity<?> createOrder(
		@RequestParam Long orderId,
		HttpServletRequest request
	) throws IOException, ApiException {
		var payment = paymentService.getPayment(orderId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(payment);
	}

	@PostMapping("/capture")
	public ResponseEntity<?> captureOrder(
		@RequestParam String transactionID,
		HttpServletRequest request
	) throws IOException, ApiException {
		var result = paymentService.capturePayment(transactionID, request);
		return ResponseEntity.ok(result);
	}
}
