package com.culcon.backend.models;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@Valid
public class AccountTest {
    private final Account validAccount = Account.builder()
            .email("test@test.com")
            .username("test")
            .password("123456")
            .role(Role.CUSTOMER)
            .build();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void createAccountNullField() {
        assertThrows(NullPointerException.class, () -> {
            Account.builder().build();
        });
    }

    @Test
    void accountEmptyEmail() {
        assertThrows(NullPointerException.class, () -> {

            validAccount.toBuilder()
                    .email(null).build();
        });
    }

    @Test
    void accountIncorrectMail() {
        var violations = validator.validate(Account.builder()
                .email("testcom")
                .username("test")
                .password("123456")
                .role(Role.CUSTOMER)
                .build());
        assertTrue(!violations.isEmpty());
    }

    @Test
    void accountEmptyUsername() {
        assertThrows(NullPointerException.class, () -> {
            validAccount.toBuilder()
                    .username(null).build();
        });
    }
}
