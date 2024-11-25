package com.culcon.backend.services;

import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface UserService {

	Map<String, Object> updateCustomer(CustomerInfoUpdateRequest newData, HttpServletRequest request);

	AuthenticationResponse updateCustomerPassword(CustomerPasswordRequest newData, HttpServletRequest request);
}
