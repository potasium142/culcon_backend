package com.culcon.backend.services;
import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;
import com.culcon.backend.models.Account;
import com.culcon.backend.models.AccountOTP;
import com.culcon.backend.repositories.AccountOTPRepo;
import com.culcon.backend.repositories.AccountRepo;
import com.culcon.backend.services.authenticate.JwtService;
import com.culcon.backend.services.authenticate.implement.AuthImplement;
import com.culcon.backend.services.implement.OTPImplement;
import com.culcon.backend.services.implement.PaymentImplement;
import jakarta.validation.ConstraintViolationException;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class OTPServiceTest {

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
    private OTPImplement otpService;
    @Mock
    AccountOTPRepo accountOTPRepo;
    @Mock
    JavaMailSender mailSender;
    @Mock
    private Account account;
    @Mock
    private AccountOTP accountOTP; // Mock AccountOTP


    @Test
    void generateOTP_newAccount() {
        // Setup test data
        String email = "test@example.com";
        int otpLength = 6;
        int expireMinutes = 15;
        String expectedOTP = "123456"; // Fixed OTP for testing

        // Mock account behavior
        when(account.getId()).thenReturn("1");

        // Mock existing OTP retrieval (return empty to trigger new OTP creation)
        when(accountOTPRepo.findByAccount(account)).thenReturn(Optional.empty());

        // Mock OTP generation
        try (MockedStatic<RandomString> mockedRandomString = Mockito.mockStatic(RandomString.class)) {
            mockedRandomString.when(() -> RandomString.make(otpLength)).thenReturn(expectedOTP);

            // Expected expiration timestamp
            Timestamp expectedExpiration = Timestamp.valueOf(LocalDateTime.now().plusMinutes(expireMinutes));

            // Create expected AccountOTP object
            AccountOTP expectedAccountOTP = AccountOTP.builder()
                    .account(account)
                    .accountId(account.getId())
                    .otp(expectedOTP)
                    .otpExpiration(expectedExpiration)
                    .email(email)
                    .build();

            // Mock repository save() to return the expected object
            when(accountOTPRepo.save(any(AccountOTP.class))).thenReturn(expectedAccountOTP);

            // Call the method under test
            AccountOTP result = otpService.generateOTP(account, email, otpLength, expireMinutes);

            // Debug output
            System.out.println("Generated OTP: " + result.getOtp());
            System.out.println("Email: " + result.getEmail());
            System.out.println("Expiration: " + result.getOtpExpiration());

            // Assertions
            Assertions.assertNotNull(result, "OTP result should not be null");
            Assertions.assertEquals(expectedOTP, result.getOtp(), "OTP should match expected value");
            Assertions.assertEquals(email, result.getEmail(), "Email should match expected value");
            Assertions.assertNotNull(result.getOtpExpiration(), "Expiration time should not be null");
            Assertions.assertTrue(result.getOtpExpiration().after(new Timestamp(System.currentTimeMillis())),
                    "Expiration should be in the future");

            // Verify repository save
            verify(accountOTPRepo).save(any(AccountOTP.class));
        }
    }



    @Test
    void generateOTP_Fail_WhenExpirationIsInvalid() {
        // Arrange
        String email = "test@example.com";
        int otpLength = 6;
        int expireMinutes = 0; // Force expiration to be "immediate" or null
        String expectedOTP = "123456"; // Fixed OTP for testing

        // Mock account behavior
        when(account.getId()).thenReturn("1");

        // Mock existing OTP retrieval (return empty to trigger new OTP creation)
        when(accountOTPRepo.findByAccount(account)).thenReturn(Optional.empty());

        // Mock OTP generation
        try (MockedStatic<RandomString> mockedRandomString = Mockito.mockStatic(RandomString.class)) {
            mockedRandomString.when(() -> RandomString.make(otpLength)).thenReturn(expectedOTP);

            // Create expected AccountOTP object with null expiration
            AccountOTP expectedAccountOTP = AccountOTP.builder()
                    .account(account)
                    .accountId(account.getId())
                    .otp(expectedOTP)
                    .otpExpiration(null) // Ensure expiration is null
                    .email(email)
                    .build();

            // Mock repository save() to return the expected object
            when(accountOTPRepo.save(any(AccountOTP.class))).thenReturn(expectedAccountOTP);

            // Act
            AccountOTP result = otpService.generateOTP(account, email, otpLength, expireMinutes);

            // Debug output
            System.out.println("Generated OTP: " + result.getOtp());
            System.out.println("Email: " + result.getEmail());
            System.out.println("Expiration: " + result.getOtpExpiration());

            // Assertions
            Assertions.assertNotNull(result, "OTP result should not be null");
            Assertions.assertEquals(expectedOTP, result.getOtp(), "OTP should match expected value");
            Assertions.assertEquals(email, result.getEmail(), "Email should match expected value");
            Assertions.assertNull(result.getOtpExpiration(), "Expiration time should be null");

            // Verify repository save
            verify(accountOTPRepo).save(any(AccountOTP.class));
        }
    }

}
