package com.culcon.backend.repositories.user;

import com.culcon.backend.models.user.AccountOTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountOTPRepo extends JpaRepository<AccountOTP, String> {
	Optional<AccountOTP> findAccountOTPByAccountId(String id);
}
