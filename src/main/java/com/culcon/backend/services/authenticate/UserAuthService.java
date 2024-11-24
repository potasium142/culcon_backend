package com.culcon.backend.services.authenticate;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserAuthService {
	UserDetailsService userDetailsServices();
}
