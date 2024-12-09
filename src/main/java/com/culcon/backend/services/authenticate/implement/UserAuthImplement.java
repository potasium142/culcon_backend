package com.culcon.backend.services.authenticate.implement;

import com.culcon.backend.repositories.AccountRepo;
import com.culcon.backend.services.authenticate.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthImplement implements UserAuthService {

	private final AccountRepo userRepository;

	@Override
	public UserDetailsService userDetailsServices() {
		return username -> userRepository
			.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
}
