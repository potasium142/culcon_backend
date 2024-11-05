package com.culcon.backend.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.Role;

@DataJpaTest

public class AccountRepoTest {
    @Autowired
    private AccountRepo accountRepo;

    @Test
    void saveAccount() {
        var admin = Account.builder()
                .email("example@admin")
                // ADMIN
                .password("$2a$10$n7NTAk2ymn6sYQEmwnqbI.mIqOBFSAWdXoZewi.PiPxQqnZiQq9zq")
                .role(Role.CUSTOMER)
                .phone("0123456789")
                .username("admin")
                .build();

        var account = accountRepo.save(admin);

        assertNotNull(account);
    }

}
