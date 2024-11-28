package com.culcon.backend.services;

import com.culcon.backend.dtos.auth.AuthenticationResponse;
import com.culcon.backend.dtos.auth.CustomerRegisterRequest;
import com.culcon.backend.models.user.Account;
import com.culcon.backend.repositories.user.AccountRepo;
import com.culcon.backend.services.authenticate.JwtService;
import com.culcon.backend.services.authenticate.implement.AuthImplement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    JwtService jwtService;
    @Mock
    AccountRepo accountRepo;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthImplement authService;

    @Test
    void authService_Register_Success() {
        var registerRequest = CustomerRegisterRequest.builder()
                .username("user01")
                .password("admin")
                .email("user01@gmail.com")
                .phone("0123456789")
                .description("")
                .address("")
                .build();

        var account = Account.builder()
                .username("user01")
                .password("admin")
                .email("user01@gmail.com")
                .phone("0123456789")
                .build();

        when(passwordEncoder.encode("admin"))
                .thenReturn("admin");
        when(accountRepo.save(account)).thenReturn(account);
        when(jwtService.generateToken(account))
                .thenReturn("when_the_token_is_sus");
        when(accountRepo.findById(account.getId()))
                .thenReturn(Optional.of(account));

        var token = authService.registerCustomer(registerRequest);

        var responseRequest = AuthenticationResponse.builder()
                .accessToken("when_the_token_is_sus")
                .build();
        Assertions.assertEquals(token, responseRequest);
    }
}
