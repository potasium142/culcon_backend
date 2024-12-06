package com.culcon.backend.models.user;

public enum OrderStatus {
	ON_CONFIRM,
	WAIT_FOR_PAYMENT,
	ON_PROCESSING,
	ON_SHIPPING,
	SHIPPED,
	CANCELLED,
}
