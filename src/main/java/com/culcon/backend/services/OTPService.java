package com.culcon.backend.services;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.AccountOTP;
import jakarta.mail.MessagingException;

import javax.security.auth.login.AccountNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

public interface OTPService {

    void generateOneTimePassword(Account account) throws MessagingException, UnsupportedEncodingException, AccountNotFoundException;

    void sendOTPEmail(AccountOTP accountOTP)  throws UnsupportedEncodingException, MessagingException, AccountNotFoundException ;

    AccountOTP getAccountOTPById(String id) throws AccountNotFoundException;

    Boolean compareOTPs(String inputOTP, String dbOTP);

    void clearOTP(AccountOTP accountOTP);

}
