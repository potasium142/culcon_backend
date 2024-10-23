package com.culcon.backend.services;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
  UserDetailsService userDetailsServices();
}
