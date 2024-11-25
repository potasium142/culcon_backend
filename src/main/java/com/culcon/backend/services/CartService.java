package com.culcon.backend.services;

import com.culcon.backend.models.CartItem;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CartService {

	List<CartItem> getUserCartItems(HttpServletRequest request);
}
