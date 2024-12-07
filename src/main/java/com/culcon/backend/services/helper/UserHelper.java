package com.culcon.backend.services.helper;

import com.culcon.backend.models.user.Account;
import com.culcon.backend.repositories.user.AccountRepo;
import com.culcon.backend.services.authenticate.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@RequiredArgsConstructor
@Transactional
@Service
public class UserHelper {
	private final AccountRepo accountRepo;
	private final JwtService jwtService;

	public Account getAccountByEmail(String email) throws AccountNotFoundException {
		return accountRepo.findAccountByEmail(email.trim())
			.orElseThrow(() -> new AccountNotFoundException("Account not found"));
	}

	public String loginByEmail(String email) throws AccountNotFoundException {
		var account = accountRepo.findAccountByEmail(email.trim())
			.orElseThrow(() -> new AccountNotFoundException("Account not found"));
		var token = jwtService.generateToken(account);

		account.setToken(token);
		accountRepo.save(account);

		return token;
	}

}
