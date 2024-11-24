package com.culcon.backend.services.implement;

import com.culcon.backend.dtos.auth.*;
import com.culcon.backend.models.Account;
import com.culcon.backend.models.Role;
import com.culcon.backend.repositories.AccountRepo;
import com.culcon.backend.services.authenticate.AuthService;
import com.culcon.backend.services.authenticate.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<Object> getUserInformation(HttpServletRequest request) {
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
			user,
			HttpStatus.OK);
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
			.role(Role.CUSTOMER)
			.address(request.address())
			.phone(request.phone())
			.profileDescription(request.description())
			.build();
		var savedUser = userRepo.save(user);
		var jwtToken = jwtService.generateToken(user);
		saveUserToken(savedUser, jwtToken);

		return AuthenticationResponse.builder().accessToken(jwtToken).build();
	}

	@Override
	public ResponseEntity<Object>  updateCustomer(CustomerInfoUpdateRequest newUserData, HttpServletRequest request) {
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


		user.setEmail(newUserData.email());
		user.setUsername(newUserData.username());
//		user.setPassword(passwordEncoder.encode(newUserData.password()));
		user.setRole(Role.CUSTOMER);
		user.setAddress(newUserData.address());
		user.setPhone(newUserData.phone());
		user.setProfileDescription(newUserData.description());

		var savedUser = userRepo.save(user);

		return new ResponseEntity<>(
				savedUser,
				HttpStatus.OK);
	}


	@Override
	public ResponseEntity<Object>  updateCustomerPassword(CustomerPasswordRequest newUserData, HttpServletRequest request) {
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

		if (passwordEncoder.matches(newUserData.oldpassword(), user.getPassword())) {
			if (newUserData.repassword().equals(newUserData.password())){
				user.setPassword(passwordEncoder.encode(newUserData.password()));
				var savedUser = userRepo.save(user);
				return new ResponseEntity<>(
						savedUser,
						HttpStatus.OK);
			}
			return new ResponseEntity<>(
					"RePassword do not match",
					HttpStatus.BAD_REQUEST);
		}


		return new ResponseEntity<>(
				"Passwords do not match",
				HttpStatus.BAD_REQUEST);
	}
}
