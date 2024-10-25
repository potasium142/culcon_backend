package com.culcon.backend.configs;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.culcon.backend.repositories.AccountRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private static final Logger logger = LoggerFactory.getLogger(LogoutService.class);
    @Autowired
    private AccountRepo userRepository;

    @Override
    public void logout(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("text/plain");
            try {
                response.getWriter().write("No JWT token found in the request header");
            } catch (IOException e) {
                logger.error("Error writing unauthorized response", e);
            }
            return;
        }
        final String jwtToken = authHeader.substring(7);
        var user = userRepository.findByToken(jwtToken).orElse(null);
        if (user != null) {
            user.setToken("");
            userRepository.save(user);
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("text/plain");
            try {
                response.getWriter().write("Logged out successfully");
            } catch (IOException e) {
                logger.error("Error writing unauthorized response", e);
            }
        }
    }
}
