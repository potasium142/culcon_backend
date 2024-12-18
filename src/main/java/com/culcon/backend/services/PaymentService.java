package com.culcon.backend.services;

import com.culcon.backend.dtos.PaymentDTO;
import com.culcon.backend.models.OrderHistory;
import com.paypal.sdk.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface PaymentService {
	void createPayment(OrderHistory order, HttpServletRequest request)
		throws IOException, ApiException;

	PaymentDTO capturePayment(String transactionId, HttpServletRequest request)
		throws IOException, ApiException;

	String getPayment(Long orderId, HttpServletRequest request) throws IOException, ApiException;

	void refund(OrderHistory order)
		throws IOException, ApiException;

	void updatePrice(OrderHistory order, Float price) throws IOException, ApiException;
}
