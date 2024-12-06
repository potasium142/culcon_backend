package com.culcon.backend.services;

import com.culcon.backend.dtos.order.OrderCreation;
import com.culcon.backend.dtos.order.OrderDetail;
import com.culcon.backend.dtos.order.OrderItemInList;
import com.culcon.backend.dtos.order.OrderSummary;
import com.culcon.backend.models.user.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {
	OrderSummary createOrder(OrderCreation orderCreation, HttpServletRequest req);

	List<OrderItemInList> getListOfOrder(HttpServletRequest req, OrderStatus status);

	List<OrderItemInList> getListOfAllOrder(HttpServletRequest req);

	OrderDetail getOrderItem(HttpServletRequest req, Long orderId);

	OrderDetail updateOrder(HttpServletRequest req, Long orderId, OrderCreation orderCreation);

	OrderDetail cancelOrder(HttpServletRequest req, Long orderId);


}
