package com.culcon.backend.services.helper;

import com.culcon.backend.models.user.Account;
import com.culcon.backend.repositories.user.AccountRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@RequiredArgsConstructor
@Transactional
@Service
public class UserHelper {
    private final AccountRepo accountRepo;

    public Account getAccountByEmail(String email) throws AccountNotFoundException {
        return accountRepo.findAccountByEmail(email.trim())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }
    
}
