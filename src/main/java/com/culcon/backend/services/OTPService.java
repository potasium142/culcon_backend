package com.culcon.backend.services;

import com.culcon.backend.models.user.Account;
import com.culcon.backend.models.user.AccountOTP;
import jakarta.mail.MessagingException;

import javax.security.auth.login.AccountNotFoundException;
import java.io.UnsupportedEncodingException;

public interface OTPService {

	void generateOneTimePassword(Account account) throws
		MessagingException, UnsupportedEncodingException, AccountNotFoundException;

	AccountOTP generateOTP(Account account,
	                       int otpLength,
	                       int expireMinutes);

	void sendOTPEmail(AccountOTP accountOTP) throws UnsupportedEncodingException, MessagingException;
}
