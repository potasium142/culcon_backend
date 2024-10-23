package com.culcon.backend.services.Impl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.culcon.backend.controllers.auth.AuthenticationRequest;
import com.culcon.backend.controllers.auth.AuthenticationResponse;
import com.culcon.backend.exceptions.CustomSuccessHandler;
import com.culcon.backend.models.Account;
import com.culcon.backend.repositories.UserRepository;
import com.culcon.backend.services.AuthService;
import com.culcon.backend.services.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthImplement implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private void saveUserToken(Account user, String jwtToken) {
        var currentUser = userRepository.findById(user.getId()).orElseThrow();
        currentUser.setToken(jwtToken);
        userRepository.save(currentUser);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticate) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticate.getUsername(), authenticate.getPassword()));
        var user = userRepository.findByUsername(authenticate.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        revokeAllUserToken(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder().accessToken(jwtToken).build();
    }

    private void revokeAllUserToken(Account user) {
        var currentUser = userRepository.findById(user.getId()).orElseThrow();
        currentUser.setToken("");
        userRepository.save(currentUser);
    }

    @Override
    public ResponseEntity<Object> getUserInformation(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("No JWT token found in the request header");
        }
        final Account user = userRepository.findByToken(token).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT is not belong to any user");
        }

        if (!jwtService.isTokenValid(token, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("JWT token has expired and revoked");
        }

        return CustomSuccessHandler.responseBuilder(
                "Successfully retrieved user information", HttpStatus.OK, user);
    }

    public String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

}
