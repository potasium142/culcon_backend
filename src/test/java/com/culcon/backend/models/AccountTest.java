package com.culcon.backend.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class AccountTest {
    private final Account validAccount = Account.builder()
            .email("test@test.com")
            .username("test")
            .password("123456")
            .role(Role.CUSTOMER)
            .address("Dong Lao City")
            .phone("0123456789")
            .build();

    private final Validator validator = Validation
            .buildDefaultValidatorFactory()
            .getValidator();

    @Test
    void accountCreation() {
        assertAll(() -> {
            assertTrue(validAccount
                    .getEmail().equals("test@test.com"));
            assertTrue(validAccount
                    .getUsername().equals("test"));
            assertTrue(validAccount
                    .getPassword().equals("123456"));
            assertTrue(validAccount
                    .getRole() == Role.CUSTOMER);
            assertTrue(validAccount.getStatus() == AccountStatus.NORMAL);
            assertTrue(validAccount.isEnabled());
            assertTrue(validAccount.isAccountNonLocked());
            assertTrue(validAccount
                    .getAddress().equals("Dong Lao City"));
            assertTrue(validAccount
                    .getPhone().equals("0123456789"));
            assertTrue(validAccount
                    .getProfilePictureUri().equals("defaultProfile"));
        });
    }

    @Test
    void accountAllFieldNull() {
        assertThrows(NullPointerException.class, () -> {
            Account.builder().build();
        });
    }

    @Test
    void accountMailEmpty() {
        assertAll(() -> {
            assertThrows(NullPointerException.class, () -> {
                validAccount.toBuilder()
                        .email(null).build();
            });

            var violations = validator.validate(
                    validAccount.toBuilder()
                            .email(" ")
                            .build());
            assertTrue(!violations.isEmpty());
        });
    }

    @Test
    void accountMailInvalid() {
        var violations = validator.validate(
                validAccount.toBuilder()
                        .email("test")
                        .build());
        assertTrue(!violations.isEmpty());
    }

    @Test
    void accountUsernameEmpty() {
        assertAll(() -> {
            assertThrows(NullPointerException.class, () -> {
                validAccount.toBuilder()
                        .username(null)
                        .build();
            });

            var violations = validator.validate(
                    validAccount.toBuilder()
                            .username(" ")
                            .build());
            assertTrue(!violations.isEmpty());
        });
    }

    @Test
    void accountPasswordEmpty() {
        assertAll(() -> {
            assertThrows(NullPointerException.class, () -> {
                validAccount.toBuilder()
                        .password(null)
                        .build();
            });

            var violations = validator.validate(
                    validAccount.toBuilder()
                            .password(" ")
                            .build());
            assertTrue(!violations.isEmpty());
        });
    }

}
