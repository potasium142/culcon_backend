package com.culcon.backend.services.implement;

import com.culcon.backend.repositories.AccountRepo;
import com.culcon.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserImplement implements UserService {

	private final AccountRepo userRepository;

	@Override
	public UserDetailsService userDetailsServices() {
		return username -> userRepository
			.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
}
