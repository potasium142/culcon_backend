package com.culcon.backend.services;

import com.culcon.backend.dtos.OrderCreation;
import com.culcon.backend.dtos.OrderSummary;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
	public OrderSummary createOrder(OrderCreation orderCreation, HttpServletRequest req);
}
