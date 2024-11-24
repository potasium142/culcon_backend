package com.culcon.backend.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface CartService {

    ResponseEntity<Object> getUserCartItems(HttpServletRequest request);
}
