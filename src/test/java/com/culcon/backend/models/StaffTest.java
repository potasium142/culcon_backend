package com.culcon.backend.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class StaffTest {
    private final Validator validator = Validation
            .buildDefaultValidatorFactory()
            .getValidator();

    private final Account validAccount = Account.builder()
            .email("test@test.com")
            .username("test")
            .password("123456")
            .role(Role.CUSTOMER)
            .build();

    private final Staff validStaff = Staff.builder()
            .account(validAccount)
            .address("Dong Lao City")
            .phone("0123456789")
            .ssn("0123456789")
            .build();

    @Test
    void accountCreation() {
        assertAll(() -> {
            assertEquals(validStaff.getAccount(), validAccount);
            assertTrue(validStaff.getAddress().equals("Dong Lao City"));
            assertTrue(validStaff.getPhone().equals("0123456789"));
            assertTrue(validStaff.getSsn().equals("0123456789"));
            assertTrue(validStaff
                    .getProfilePictureUri().equals("defaultProfile"));
            assertTrue(validator.validate(validStaff).isEmpty());
        });
    }

    @Test
    void accountCreationDefault() {
        var cus = Customer.builder().account(validAccount).build();
        assertAll(() -> {
            assertEquals(cus.getAccount(), validAccount);
            assertTrue(cus.getAddress().equals(""));
            assertTrue(cus.getPhone().equals(""));
            assertTrue(cus.getProfilePictureUri().equals("defaultProfile"));
            assertFalse(validator.validate(cus).isEmpty());
        });
    }
}
