package com.culcon.backend.services;

import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import com.culcon.backend.models.user.Account;
import jakarta.servlet.http.HttpServletRequest;

import javax.security.auth.login.AccountNotFoundException;

public interface UserService {

	Account getAccountByEmail(String email) throws AccountNotFoundException;

	Account updateCustomer(CustomerInfoUpdateRequest newData, HttpServletRequest request);

	AuthenticationResponse updateCustomerPassword(CustomerPasswordRequest newData, HttpServletRequest request);

	void updateCustomerPasswordOTP(String otp, String id, String newPassword);

}
