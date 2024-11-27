package com.culcon.backend.services.implement;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.AccountOTP;
import com.culcon.backend.repositories.AccountOTPRepo;
import com.culcon.backend.services.OTPService;
import com.culcon.backend.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OTPImplement implements OTPService {


    private final AccountOTPRepo accountOTPRepo;

    private final JavaMailSender mailSender;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;



    @Override
    public void generateOneTimePassword(Account account) throws MessagingException, UnsupportedEncodingException, AccountNotFoundException {
        AccountOTP accountOTP = new AccountOTP();

        accountOTP.setAccountId(account.getId());

        String OTP = RandomString.make(8);
//        String encodedOTP = passwordEncoder.encode(OTP);

        accountOTP.setOtp(OTP);

        // Get the current date
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Convert to java.sql.Date
        Timestamp sqlTimestamp = Timestamp.valueOf(currentDateTime);


        accountOTP.setOtpExpiration(sqlTimestamp);

        accountOTPRepo.save(accountOTP);
        sendOTPEmail(accountOTP);
    }

    public void sendOTPEmail(AccountOTP accountOTP)
            throws UnsupportedEncodingException, MessagingException, AccountNotFoundException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(new InternetAddress("huynhhuyhuy12@gmail.com","Cucon.com"));
        Account account = userService.getAccountById(accountOTP.getAccountId());

        helper.setTo(account.getEmail());

        String subject = "Here's your One Time Password (OTP) - Expire in 5 minutes!";

        String content = "<p>Hello " + account.getUsername() + "</p>"
                + "<p>For security reason, you're required to use the following "
                + "One Time Password to login:</p>"
                + "<p><b>" + accountOTP.getOtp() + "</b></p>"
                + "<br>"
                + "<p>Note: this OTP is set to expire in 5 minutes.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);
        mailSender.send(message);
    }

    @Override
    public AccountOTP getAccountOTPById(String id) throws AccountNotFoundException {
        return accountOTPRepo.findAccountOTPByAccountId(id)
                .orElseThrow(() -> new AccountNotFoundException("AccountOTP not found"));
    }

    @Override
    public Boolean compareOTPs(String inputOTP, String dbOTP) {
//        if (passwordEncoder.matches(inputOTP, dbOTP)) {
//            return true;
//        }
        if (inputOTP.equals(dbOTP)){
            return true;
        }
        return false;
    }

    @Override
    public void clearOTP(AccountOTP account) {
        account.setOtp(null);
        account.setOtpExpiration(null);
        accountOTPRepo.save(account);
    }
}
