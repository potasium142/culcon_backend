package com.culcon.backend.controllers.customer;


import com.culcon.backend.services.PaymentService;
import com.paypal.sdk.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@GetMapping("/get")
	public ResponseEntity<?> createOrder(
			@RequestParam String orderId,
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

	@GetMapping("/vnpay/get")
	public ResponseEntity<?> createOrderVNPay(
			@RequestParam String orderId,
			HttpServletRequest request
	) throws IOException, ApiException {
		return ResponseEntity.status(HttpStatus.OK).body(paymentService.getPaymentVNPay(orderId, request));

	}


	// what result url look like =>	http://localhost:8080/vnpay_jsp/vnpay_return.jsp?vnp_Amount=1000000&vnp_BankCode=NCB&vnp_BankTranNo=VNP14778261&vnp_CardType=ATM&vnp_OrderInfo=Thanh+toan+don+hang%3A59049609&vnp_PayDate=20250102201754&vnp_ResponseCode=00&vnp_TmnCode=PCB8RZTL&vnp_TransactionNo=14778261&vnp_TransactionStatus=00&vnp_TxnRef=59049609&vnp_SecureHash=638f986d2e3e75c1b8bbb81db30a566a4de5cadb13adda9f4c2efd3b43c3af08e8ca3a4d17696361610f33d53693d967a7e8334f00e7169116a00c16874f40dd
	@PostMapping("/vnpay/capture")
	public ResponseEntity<?> captureOrderVNPay(
			@RequestParam String vnp_ResponseCode,

			@RequestParam String vnp_Amount,
			@RequestParam String vnp_BankTranNo,
			@RequestParam String vnp_BankCode,
			@RequestParam String vnp_OrderInfo,
			@RequestParam String vnp_TransactionNo,


			HttpServletResponse response,
			HttpServletRequest request

	) throws IOException, ApiException {

		if ("00".equals(vnp_ResponseCode)) {
//			response.sendRedirect("http://localhost:3000/invoice");
			response.sendRedirect("https://culcon-user-fe-30883260979.asia-east2.run.app/invoice");

		} else {
//			response.sendRedirect("http://localhost:3000/failure");
//			response.sendRedirect("http://localhost:3000/invoice");
			response.sendRedirect("https://culcon-user-fe-30883260979.asia-east2.run.app/invoice");
		}

		Map<String, String> params = new HashMap<>();
		params.put("vnp_ResponseCode", vnp_ResponseCode);
		params.put("vnp_Amount", vnp_Amount);
		params.put("vnp_BankTranNo", vnp_BankTranNo);
		params.put("vnp_BankCode", vnp_BankCode);
		params.put("vnp_OrderInfo", vnp_OrderInfo);
		params.put("vnp_TransactionNo", vnp_TransactionNo);

		return ResponseEntity.ok(params);
	}

}
