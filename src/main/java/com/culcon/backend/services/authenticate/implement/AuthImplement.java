package com.culcon.backend.services.authenticate.implement;

import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;
import com.culcon.backend.models.Account;
import com.culcon.backend.repositories.AccountRepo;
import com.culcon.backend.services.authenticate.AuthService;
import com.culcon.backend.services.authenticate.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthImplement implements AuthService {

	private final AccountRepo userRepo;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;

	public void saveUserToken(Account user, String jwtToken) {
		var currentUser = userRepo.findById(user.getId()).orElseThrow();
		currentUser.setToken(jwtToken);
		userRepo.save(currentUser);
	}

	@Override
	public AuthenticationResponse authenticate(AuthenticationRequest authenticate) {
		var user = userRepo
			.findByUsername(authenticate.username())
			.orElseThrow(
				() -> new NoSuchElementException("No account with such username"));

		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				authenticate.username(),
				authenticate.password()));

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
	public Account getUserInformation(HttpServletRequest request) {
		String token = extractTokenFromHeader(request);
		if (token == null) {
			throw new AccountExpiredException("JWT token is not valid");
		}

		var user = userRepo
			.findByToken(token)
			.orElseThrow(() -> new AccountExpiredException("No account with such username"));

		if (!jwtService.isTokenValid(token, user)) {
			throw new AccountExpiredException("Expired JWT token");
		}

		return user;
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
			.address(request.address())
			.phone(request.phone())
			.profileName(request.profileName())
			.profileDescription(request.description())
			.build();
		var savedUser = userRepo.save(user);
		var jwtToken = jwtService.generateToken(user);
		saveUserToken(savedUser, jwtToken);

		return AuthenticationResponse.builder().accessToken(jwtToken).build();
	}

}
