package com.culcon.backend.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.culcon.backend.repositories.UserRepository;
import com.culcon.backend.services.UserService;

@Service
@RequiredArgsConstructor
public class UserImplement implements UserService {

  private final UserRepository userRepository;

  @Override
  public UserDetailsService userDetailsServices() {
    return username ->
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}
