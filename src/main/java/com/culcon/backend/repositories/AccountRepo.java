package com.culcon.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.culcon.backend.models.Account;

public interface AccountRepo extends JpaRepository<Account, String> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findByToken(String token);

    Boolean existsByUsername(String username);

}