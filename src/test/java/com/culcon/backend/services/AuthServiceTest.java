package com.culcon.backend.services;

import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;
import com.culcon.backend.models.Account;
import com.culcon.backend.repositories.AccountRepo;
import com.culcon.backend.services.authenticate.JwtService;
import com.culcon.backend.services.authenticate.implement.AuthImplement;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;


import static org.mockito.Mockito.*;

import java.util.UUID;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	JwtService jwtService;
	@Mock
	AccountRepo accountRepo;
	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	AuthenticationManager authenticationManager;

	@Mock
	HttpServletRequest request;

	@InjectMocks
	private AuthImplement authService;



	@Test
	void authService_Register_Success() {
		var registerRequest = CustomerRegisterRequest.builder()
			.username("user01")
			.password("admin")
			.email("user01@gmail.com")
			.phone("0123456789")
			.description("")
			.address("")
			.build();

		var account = Account.builder()
			.username("user01")
			.password("admin")
			.email("user01@gmail.com")
			.phone("0123456789")
			.build();

		when(passwordEncoder.encode("admin"))
			.thenReturn("admin");
		when(accountRepo.save(account)).thenReturn(account);
		when(jwtService.generateToken(account))
			.thenReturn("when_the_token_is_sus");
		when(accountRepo.findById(account.getId()))
			.thenReturn(Optional.of(account));

		var token = authService.registerCustomer(registerRequest);

		var responseRequest = AuthenticationResponse.builder()
			.accessToken("when_the_token_is_sus")
			.build();
		Assertions.assertEquals(token, responseRequest);
	}
	@Test
	void authService_Register_Fail_AlreadyExisted() {

		var registerRequest = CustomerRegisterRequest.builder()
				.username("user01")
				.password("admin")
				.email("user01@gmail.com")
				.phone("0123456789")
				.build();
		when(passwordEncoder.encode("admin"))
				.thenReturn("admin");

		doThrow(new DataIntegrityViolationException("Unique constraint violation"))
				.when(accountRepo)
				.save(any(Account.class));

		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			authService.registerCustomer(registerRequest);
		});

		verify(accountRepo, times(1)).save(any(Account.class));
	}

	//@Test
	void authService_authenticate_Success() {
		var loginRequest = AuthenticationRequest.builder()
				.username("user01")
				.password("admin")
				.build();

		var account = Account.builder()
				.username("user01")
				.password("admin")
				.email("user01@gmail.com")
				.phone("0123456789")
				.build();

		when(accountRepo.save(account)).thenReturn(account);
		when(jwtService.generateToken(account))
				.thenReturn("when_the_token_is_sus");
		when(accountRepo.findByUsername(account.getUsername()))
				.thenReturn(Optional.of(account));
		when(		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						account.getUsername(),
						account.getPassword())))
				.thenReturn(null);

		var token = authService.authenticate(loginRequest);

		var responseRequest = AuthenticationResponse.builder()
				.accessToken("when_the_token_is_sus")
				.build();

		Assertions.assertEquals(token, responseRequest);
	}

	@Test
	void authService_authenticate_Fail() {
		var loginRequest = AuthenticationRequest.builder()
				.username("user01")
				.password("admin")
				.build();


		Assertions.assertThrows(java.util.NoSuchElementException.class, () -> {
			authService.authenticate(loginRequest);
		});
	}


	@Test
	void authService_getUserInformation_Success() {

		var account = Account.builder()
				.username("user01")
				.password("admin")
				.email("user01@gmail.com")
				.phone("0123456789")
				.build();

		when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer when_the_token_is_sus");
		when(accountRepo.save(account)).thenReturn(account);
		accountRepo.save(account);
		when(jwtService.generateToken(account))
				.thenReturn("when_the_token_is_sus");
		account.setToken(jwtService.generateToken(account));


		when(accountRepo.findByToken(account.getToken()))
				.thenReturn(Optional.of(account));

//		ignore the checking valid token from bean
		when(jwtService.isTokenValid(account.getToken(), account))
				.thenReturn(true);

		var accountResult = authService.getUserInformation(request);

		var responseRequest = account;
		Assertions.assertEquals(accountResult, responseRequest);
	}

	@Test
	void authService_getUserInformation_Fail() {

		Assertions.assertThrows(org.springframework.security.authentication.AccountExpiredException.class, () -> {
			authService.getUserInformation(request);
		});
	}
}
