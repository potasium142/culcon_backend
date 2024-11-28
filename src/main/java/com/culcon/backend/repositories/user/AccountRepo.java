package com.culcon.backend.repositories.user;

import com.culcon.backend.models.user.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account, String> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findAccountById(String id);
    Optional<Account> findAccountByEmail(String email);

    Optional<Account> findByToken(String token);

    Boolean existsByUsername(String username);

}