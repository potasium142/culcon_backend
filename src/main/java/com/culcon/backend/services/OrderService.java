package com.culcon.backend.services;

import com.culcon.backend.dtos.CouponDTO;
import com.culcon.backend.dtos.order.*;
import com.culcon.backend.models.OrderStatus;
import com.culcon.backend.models.PaymentMethod;
import com.paypal.sdk.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

public interface OrderService {
	OrderSummary createOrder(OrderCreation orderCreation, HttpServletRequest req) throws IOException, ApiException;

	List<OrderInList> getListOfOrderByStatus(HttpServletRequest req, OrderStatus status);

	List<OrderInList> getListOfAllOrder(HttpServletRequest req);

	OrderDetail getOrderDetail(HttpServletRequest req, String orderId);

	CouponDTO updateOrderCoupon(HttpServletRequest req, String orderId, String couponId) throws IOException, ApiException;

	OrderSummary changePayment(HttpServletRequest req, String orderId, PaymentMethod paymentMethod) throws IOException, ApiException;

	OrderSummary updateOrder(HttpServletRequest req, String orderId, OrderUpdate orderCreation);

	OrderSummary cancelOrder(HttpServletRequest req, String orderId);


}
