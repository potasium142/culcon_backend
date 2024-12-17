package com.culcon.backend.services;

import com.culcon.backend.dtos.PaymentDTO;
import com.culcon.backend.models.OrderHistory;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.models.Order;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface PaymentService {
	Order createPayment(Long orderId, HttpServletRequest request)
		throws IOException, ApiException;

	PaymentDTO capturePayment(String transactionId, HttpServletRequest request)
		throws IOException, ApiException;

	void refund(OrderHistory order)
		throws IOException, ApiException;
}
