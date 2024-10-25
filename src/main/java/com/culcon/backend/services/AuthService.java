package com.culcon.backend.services;

import org.springframework.http.ResponseEntity;

import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticate);

    ResponseEntity<Object> getUserInformation(HttpServletRequest request);

    AuthenticationResponse registerCustomer(CustomerRegisterRequest request);

}
