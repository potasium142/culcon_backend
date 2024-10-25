package com.culcon.backend.controllers.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.culcon.backend.configs.LogoutService;
import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;
import com.culcon.backend.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    private final LogoutService logoutService;

    @Operation(tags = { "Authentication" })
    @PostMapping("/register/customer")
    public ResponseEntity<Object> registerCustomer(
            @Valid @RequestBody CustomerRegisterRequest request) {
        var registerLoginToken = authService.registerCustomer(request);
        return new ResponseEntity<>(
                registerLoginToken,
                HttpStatus.OK);
    }

    @Operation(tags = { "Authentication" })
    @PostMapping("/signin")
    public ResponseEntity<Object> signIn(@RequestBody @Valid AuthenticationRequest authenticate) {
        var authenStatus = authService.authenticate(authenticate);
        return new ResponseEntity<>(authenStatus, HttpStatus.OK);
    }

    @Operation(tags = { "Authentication" })
    @PostMapping("/logout")
    public void logout(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutService.logout(request, response, authentication);
    }

    @Operation(tags = { "Account" })
    @GetMapping("/account")
    public ResponseEntity<Object> getCurrentLoginUser(HttpServletRequest request) {
        return authService.getUserInformation(request);
    }
}
