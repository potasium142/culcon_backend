package com.culcon.backend.services;

import org.springframework.http.ResponseEntity;

import com.culcon.backend.controllers.auth.AuthenticationRequest;
import com.culcon.backend.controllers.auth.AuthenticationResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticate);

    ResponseEntity<Object> getUserInformation(HttpServletRequest request);
}
