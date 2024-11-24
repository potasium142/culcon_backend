package com.culcon.backend.services.authenticate;

import com.culcon.backend.dtos.auth.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
	AuthenticationResponse authenticate(AuthenticationRequest authenticate);

	ResponseEntity<Object> getUserInformation(HttpServletRequest request);

	AuthenticationResponse registerCustomer(CustomerRegisterRequest request);

	ResponseEntity<Object>  updateCustomer(CustomerInfoUpdateRequest newData, HttpServletRequest request);

	ResponseEntity<Object>  updateCustomerPassword(CustomerPasswordRequest newData, HttpServletRequest request);
}
