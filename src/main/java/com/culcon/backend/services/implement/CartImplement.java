package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.repositories.AccountRepo;
import com.culcon.backend.repositories.CartRepo;
import com.culcon.backend.services.CartService;
import com.culcon.backend.services.authenticate.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CartImplement  implements CartService {
    private final CartRepo cartRepo;
    private final AccountRepo userRepo;
    private final JwtService jwtService;

    public String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    @Override
    public ResponseEntity<Object> getUserCartItems(HttpServletRequest request){
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
                cartRepo.findByAccountId(user.getId()),
                HttpStatus.OK);
    }
}
