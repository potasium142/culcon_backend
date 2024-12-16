package com.culcon.backend.controllers.customer;


import com.culcon.backend.repositories.PaymentTransactionRepo;
import com.culcon.backend.services.PaymentService;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.controllers.PaymentsController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.CapturesRefundInput;
import com.paypal.sdk.models.Order;
import com.paypal.sdk.models.OrdersCaptureInput;
import com.paypal.sdk.models.Refund;
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

	private final PaypalServerSdkClient client;
	private final PaymentService paymentService;
	private final PaymentTransactionRepo paymentTransactionRepo;

	@PostMapping("/orders")
	public ResponseEntity<?> createOrder(
		@RequestParam Long orderId,
		@RequestParam Float value,
		HttpServletRequest request
	) throws IOException, ApiException {
		var payment = paymentService.createPayment(orderId, value, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(payment);
	}


	@PostMapping("/orders/{orderID}/capture")
	public ResponseEntity<Order> captureOrder(@PathVariable String orderID) {
		try {
			OrdersCaptureInput ordersCaptureInput = new OrdersCaptureInput.Builder(
				orderID,
				null)
				.build();
			OrdersController ordersController = client.getOrdersController();
			ApiResponse<Order> apiResponse = ordersController.ordersCapture(ordersCaptureInput);
			var result = apiResponse.getResult();

			var paymentTransaction = paymentTransactionRepo.findByTransactionId(result.getId());

			var paymentId = result.getPurchaseUnits().getFirst().getId();

			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PostMapping("/payments/refund/{id}")
	public ResponseEntity<Refund> refundCapturedPayment(@PathVariable String id) {
		try {
			Refund response = refundCapturedPayments(id);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Refund refundCapturedPayments(String capturedPaymentId) throws IOException, ApiException {
		PaymentsController paymentsController = client.getPaymentsController();
		CapturesRefundInput capturesRefundInput = new CapturesRefundInput.Builder(
			capturedPaymentId,
			null).build();
		ApiResponse<Refund> refundApiResponse = paymentsController.capturesRefund(capturesRefundInput);
		return refundApiResponse.getResult();
	}

}
