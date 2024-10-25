package com.culcon.backend.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CustomerTest {

    private final Account validAccount = Account.builder()
            .email("test@test.com")
            .username("test")
            .password("123456")
            .role(Role.CUSTOMER)
            .build();

    private final Customer validCustomer = Customer.builder()
            .account(validAccount)
            .address("Dong Lao City")
            .phone("0123456789")
            .build();

    @Test
    void accountCreation() {
        assertAll(() -> {
            assertEquals(validCustomer.getAccount(), validAccount);
            assertTrue(validCustomer
                    .getAddress().equals("Dong Lao City"));
            assertTrue(validCustomer
                    .getPhone().equals("0123456789"));
            assertTrue(validCustomer
                    .getProfilePictureUri().equals("defaultProfile"));
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
        });
    }
}
