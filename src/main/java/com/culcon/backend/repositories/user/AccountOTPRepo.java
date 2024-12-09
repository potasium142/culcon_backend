package com.culcon.backend.repositories.user;

import com.culcon.backend.models.user.Account;
import com.culcon.backend.models.user.AccountOTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountOTPRepo extends JpaRepository<AccountOTP, String> {

	Optional<AccountOTP> findByOtp(String otp);

	Optional<AccountOTP> findByOtpAndAccountId(String otp, String accountId);

	Optional<AccountOTP> findAccountOTPByOtpAndAccountIdAndEmail(String otp, String accountId, String email);


	Optional<AccountOTP> findByAccount(Account account);
}
