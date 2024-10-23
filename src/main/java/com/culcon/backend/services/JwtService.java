package com.culcon.backend.services;

import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
  String extractUsername(String jwtToken);

  boolean isTokenValid(String token, UserDetails userDetails);

  String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

  String generateToken(UserDetails userDetails);
}
