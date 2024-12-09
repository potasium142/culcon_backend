package com.culcon.backend.services;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.AccountOTP;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface OTPService {


	AccountOTP generateOTP(Account account,
	                       int otpLength,
	                       int expireMinutes);

	void sendOTPEmail(AccountOTP accountOTP) throws UnsupportedEncodingException, MessagingException;
}
