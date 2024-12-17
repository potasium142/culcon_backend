package com.culcon.backend.services;

import com.culcon.backend.dtos.order.*;
import com.culcon.backend.models.OrderStatus;
import com.paypal.sdk.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

public interface OrderService {
	OrderSummary createOrder(OrderCreation orderCreation, HttpServletRequest req);

	List<OrderInList> getListOfOrderByStatus(HttpServletRequest req, OrderStatus status);

	List<OrderInList> getListOfAllOrder(HttpServletRequest req);

	OrderDetail getOrderItem(HttpServletRequest req, Long orderId);

	OrderDetail updateOrder(HttpServletRequest req, Long orderId, OrderUpdate orderCreation) throws IOException, ApiException;

	OrderDetail cancelOrder(HttpServletRequest req, Long orderId);


}
