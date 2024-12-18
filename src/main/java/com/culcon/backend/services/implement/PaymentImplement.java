package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.PaymentDTO;
import com.culcon.backend.models.OrderHistory;
import com.culcon.backend.models.PaymentStatus;
import com.culcon.backend.models.PaymentTransaction;
import com.culcon.backend.repositories.OrderHistoryRepo;
import com.culcon.backend.repositories.PaymentTransactionRepo;
import com.culcon.backend.services.PaymentService;
import com.culcon.backend.services.authenticate.AuthService;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.controllers.PaymentsController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import com.paypal.sdk.utilities.JsonValue;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Transactional
public class PaymentImplement implements PaymentService {

	private final PaypalServerSdkClient client;
	private final PaymentTransactionRepo paymentTransactionRepo;
	private final OrderHistoryRepo orderHistoryRepo;
	private final AuthService authService;


	@Override
	public Order createPayment(OrderHistory order,
	                           HttpServletRequest request)
		throws IOException, ApiException {
		var ordersController = client.getOrdersController();

		var existPayment = paymentTransactionRepo.existsByOrder(order);

		if (existPayment) {
			throw new RuntimeException("Payment already exists");
		}

		OrdersCreateInput ordersCreateInput = new OrdersCreateInput.Builder(
			null,
			new OrderRequest.Builder(
				CheckoutPaymentIntent.fromString("CAPTURE"),
				Collections.singletonList(
					new PurchaseUnitRequest.Builder(
						new AmountWithBreakdown.Builder(
							"USD",
							order.getTotalPrice().toString()
						).build()
					).build()
				)
			).build()
		).build();

		ApiResponse<Order> apiResponse = ordersController.ordersCreate(ordersCreateInput);

		var result = apiResponse.getResult();

		var paymentTransaction = PaymentTransaction.builder()
			.order(order)
			.amount(order.getTotalPrice())
			.transactionId(result.getId())
			.build();

		paymentTransactionRepo.save(paymentTransaction);

		return result;
	}


	@Override
	public PaymentDTO capturePayment(String transactionId, HttpServletRequest request)
		throws IOException, ApiException {
		OrdersCaptureInput ordersCaptureInput = new OrdersCaptureInput.Builder(
			transactionId, null)
			.build();

		OrdersController ordersController = client.getOrdersController();
		ApiResponse<Order> apiResponse = ordersController.ordersCapture(ordersCaptureInput);
		var result = apiResponse.getResult();

		var paymentTransaction = paymentTransactionRepo.findByTransactionId(result.getId())
			.orElseThrow(() -> new NoSuchElementException("Transaction not found"));

		var paymentId = result
			.getPurchaseUnits().getFirst()
			.getPayments()
			.getCaptures().getFirst()
			.getId();

		paymentTransaction.setPaymentId(paymentId);

		paymentTransactionRepo.save(paymentTransaction);

		var order = paymentTransaction.getOrder();

		order.setPaymentStatus(PaymentStatus.RECEIVED);

		orderHistoryRepo.save(order);

		return PaymentDTO.from(paymentTransaction);
	}

	@Override
	public PaymentDTO getPayment(Long orderId, HttpServletRequest request) {
		var account = authService.getUserInformation(request);
		var pt = paymentTransactionRepo.findByIdAndOrder_User(orderId, account)
			.orElseThrow(() -> new NoSuchElementException("Transaction of this order is not found"));

		return PaymentDTO.from(pt);
	}

	@Async
	@Override
	public void refund(OrderHistory order) throws IOException, ApiException {
		var paymentTransactionO = paymentTransactionRepo
			.findByOrder(order);

		if (paymentTransactionO.isEmpty()) {
			return;
		}

		var paymentTransaction = paymentTransactionO.get();

		if (paymentTransaction.getStatus() == PaymentStatus.CREATED) {
			paymentTransactionRepo.delete(paymentTransaction);
			return;
		}

		PaymentsController paymentsController = client.getPaymentsController();

		CapturesRefundInput capturesRefundInput = new CapturesRefundInput.Builder(
			paymentTransaction.getPaymentId(), null).build();

		ApiResponse<Refund> refundApiResponse = paymentsController.capturesRefund(capturesRefundInput);

		paymentTransaction.setStatus(PaymentStatus.REFUNDED);
		paymentTransaction.setRefundId(refundApiResponse.getResult().getId());

		paymentTransactionRepo.save(paymentTransaction);

	}

	@Async
	@Override
	public void updatePrice(OrderHistory order, Float price) throws IOException, ApiException {
		var ordersController = client.getOrdersController();

		var ptO = paymentTransactionRepo.findByOrder(order);

		if (ptO.isEmpty()) {
			return;
		}

		var pt = ptO.get();

		OrdersPatchInput ordersPatchInput = new OrdersPatchInput.Builder(
			pt.getTransactionId(), null)
			.body(Collections.singletonList(
				new Patch.Builder(PatchOp.REPLACE)
					.path("/purchase_units/@reference_id=='default'/amount")
					.value(JsonValue.fromObject(
						"{\"currency_code\":\"USD\"" +
							",\"value\":" + price + "}"
					))
					.build()
			))
			.build();

		ordersController.ordersPatch(ordersPatchInput);
	}

}
