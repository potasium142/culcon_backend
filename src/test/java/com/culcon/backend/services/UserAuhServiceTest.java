package com.culcon.backend.services;

import com.culcon.backend.models.Account;
import com.culcon.backend.repositories.AccountRepo;

import com.culcon.backend.services.authenticate.implement.UserAuthImplement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserAuhServiceTest {

    @Mock
    AccountRepo accountRepo;

    @InjectMocks
    private UserAuthImplement userAuthService;


//    @Test
//    void userAuthService_userDetailsServices_Success() {
//        var account = Account.builder()
//                .username("user01")
//                .password("admin")
//                .email("user01@gmail.com")
//                .phone("0123456789")
//                .build();
//
//        when(accountRepo.save(account)).thenReturn(account);
////        accountRepo.save(account);
//
//        when(accountRepo.findByUsername(account.getUsername()))
//                .thenReturn(Optional.of(account));
//
//
//        var accountResult = userAuthService.userDetailsServices();
//
//
//        var responseRequest = Optional.of(account);
//
//        Assertions.assertEquals(responseRequest, accountResult);
//    }


}