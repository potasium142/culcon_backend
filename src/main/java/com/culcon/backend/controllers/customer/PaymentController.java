package com.culcon.backend.controllers.customer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.controllers.PaymentsController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

	private final ObjectMapper objectMapper;
	private final PaypalServerSdkClient client;

	@PostMapping("/orders")
	public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
		try {
			String cart = objectMapper.writeValueAsString(request.get("cart"));
			Order response = createOrder(cart);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Order createOrder(String cart) throws IOException, ApiException {
		OrdersCreateInput ordersCreateInput = new OrdersCreateInput.Builder(
			null,
			new OrderRequest.Builder(
				CheckoutPaymentIntent.fromString("CAPTURE"),
				Arrays.asList(
					new PurchaseUnitRequest.Builder(
						new AmountWithBreakdown.Builder(
							"USD",
							"100"
						).build()
					).build()
				)
			)

				.build()
		).build();
		OrdersController ordersController = client.getOrdersController();
		ApiResponse<Order> apiResponse = ordersController.ordersCreate(ordersCreateInput);
		return apiResponse.getResult();
	}

	@PostMapping("/orders/{orderID}/capture")
	public ResponseEntity<Order> captureOrder(@PathVariable String orderID) {
		try {
			Order response = captureOrders(orderID);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Order captureOrders(String orderID) throws IOException, ApiException {
		OrdersCaptureInput ordersCaptureInput = new OrdersCaptureInput.Builder(
			orderID,
			null)
			.build();
		OrdersController ordersController = client.getOrdersController();
		ApiResponse<Order> apiResponse = ordersController.ordersCapture(ordersCaptureInput);
		return apiResponse.getResult();
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
