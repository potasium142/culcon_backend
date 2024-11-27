package com.culcon.backend.services;

import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerInfoUpdateRequest;
import com.culcon.backend.dtos.auth.CustomerPasswordRequest;
import com.culcon.backend.models.Account;
import jakarta.servlet.http.HttpServletRequest;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Map;
import java.util.Optional;

public interface UserService {

	Account getAccountByEmail(String email) throws AccountNotFoundException;

	Account getAccountById(String id) throws AccountNotFoundException;

	Map<String, Object> updateCustomer(CustomerInfoUpdateRequest newData, HttpServletRequest request);

	AuthenticationResponse updateCustomerPassword(CustomerPasswordRequest newData, HttpServletRequest request);

	void updateCustomerPasswordOTP(String newPassword, Account account);

	Boolean comparePasswords(String newpass, String oldpass);


}
