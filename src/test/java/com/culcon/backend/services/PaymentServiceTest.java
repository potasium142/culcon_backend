package com.culcon.backend.services;
import com.culcon.backend.dtos.auth.AuthenticationRequest;
import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;
import com.culcon.backend.models.Account;
import com.culcon.backend.repositories.AccountRepo;
import com.culcon.backend.repositories.OrderHistoryRepo;
import com.culcon.backend.repositories.PaymentTransactionRepo;
import com.culcon.backend.services.authenticate.AuthService;
import com.culcon.backend.services.authenticate.JwtService;
import com.culcon.backend.services.authenticate.implement.AuthImplement;
import com.culcon.backend.services.implement.PaymentImplement;
import com.paypal.sdk.PaypalServerSdkClient;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

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
    private PaymentImplement paymentService;
    @Mock
    PaypalServerSdkClient client;
    @Mock
    PaymentTransactionRepo paymentTransactionRepo;
    @Mock
    OrderHistoryRepo orderHistoryRepo;
    @Mock
    AuthService authService;

}
