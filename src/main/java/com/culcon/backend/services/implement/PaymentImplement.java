package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.PaymentDTO;
import com.culcon.backend.models.PaymentTransaction;
import com.culcon.backend.repositories.OrderHistoryRepo;
import com.culcon.backend.repositories.PaymentTransactionRepo;
import com.culcon.backend.services.PaymentService;
import com.culcon.backend.services.authenticate.AuthService;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class PaymentImplement implements PaymentService {

	private final PaypalServerSdkClient client;
	private final PaymentTransactionRepo paymentTransactionRepo;
	private final OrderHistoryRepo orderHistoryRepo;
	private final AuthService authService;


	@Override
	public Order createPayment(Long orderId, Float amount,
	                           HttpServletRequest request)
		throws IOException, ApiException {
		var ordersController = client.getOrdersController();
		var account = authService.getUserInformation(request);

		var order = orderHistoryRepo.findByIdAndUser(orderId, account).orElseThrow(
			() -> new NoSuchElementException("Order not found")
		);

		OrdersCreateInput ordersCreateInput = new OrdersCreateInput.Builder(
			null,
			new OrderRequest.Builder(
				CheckoutPaymentIntent.fromString("CAPTURE"),
				Collections.singletonList(
					new PurchaseUnitRequest.Builder(
						new AmountWithBreakdown.Builder(
							"USD",
							amount.toString()
						).build()
					).build()
				)
			).build()
		).build();

		ApiResponse<Order> apiResponse = ordersController.ordersCreate(ordersCreateInput);

		var result = apiResponse.getResult();

		var paymentTransaction = PaymentTransaction.builder()
			.order(order)
			.amount(amount)
			.transactionId(result.getId())
			.build();

		paymentTransactionRepo.save(paymentTransaction);

		return result;
	}

	@Override
	public ApiResponse<Order> getPayment(String paymentId, HttpServletRequest request) {
		return null;
	}

	@Override
	public PaymentDTO capturePayment(String paymentId, HttpServletRequest request) {
		return null;
	}

}
