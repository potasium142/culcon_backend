package com.culcon.backend.services.Impl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;
import com.culcon.backend.models.Account;
import com.culcon.backend.models.Customer;
import com.culcon.backend.models.Role;
import com.culcon.backend.repositories.AccountRepo;
import com.culcon.backend.repositories.CustomerRepo;
import com.culcon.backend.services.AuthService;
import com.culcon.backend.services.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthImplement implements AuthService {

    private final AccountRepo userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepo customerRepo;

    private void saveUserToken(Account user, String jwtToken) {
        var currentUser = userRepo.findById(user.getId()).orElseThrow();
        currentUser.setToken(jwtToken);
        userRepo.save(currentUser);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticate) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticate.username(),
                        authenticate.password()));

        var user = userRepo
                .findByUsername(authenticate.username())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        revokeAllUserToken(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    private void revokeAllUserToken(Account user) {
        var currentUser = userRepo
                .findById(user.getId())
                .orElseThrow();
        currentUser.setToken("");
        userRepo.save(currentUser);
    }

    @Override
    public ResponseEntity<Object> getUserInformation(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("No JWT token found in the request header");
        }

        var user = userRepo.findByToken(token).orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("JWT is not belong to any user");
        }

        if (!jwtService.isTokenValid(token, user)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("JWT token has expired and revoked");
        }

        return new ResponseEntity<>(
                user,
                HttpStatus.OK);
    }

    public String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    public AuthenticationResponse registerCustomer(CustomerRegisterRequest request) {
        var user = Account.builder()
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.CUSTOMER)
                .build();
        var customerInfo = Customer.builder()
                .account(user)
                .address(request.address())
                .phone(request.phone())
                .build();
        var savedUser = userRepo.save(user);
        customerRepo.save(customerInfo);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder().accessToken(jwtToken).build();
    }
}
