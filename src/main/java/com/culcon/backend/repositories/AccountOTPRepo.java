package com.culcon.backend.repositories;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.AccountOTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountOTPRepo extends JpaRepository<AccountOTP, String> {
    Optional<AccountOTP> findAccountOTPByAccountId(String id);
}
