package com.culcon.backend.services;

import com.culcon.backend.dtos.PaymentDTO;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.Order;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface PaymentService {
	Order createPayment(Long orderId, Float amount, HttpServletRequest request)
		throws IOException, ApiException;

	ApiResponse<Order> getPayment(String paymentId, HttpServletRequest request);

	PaymentDTO capturePayment(String paymentId, HttpServletRequest request);
}
