package com.culcon.backend.repositories;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepo extends JpaRepository<CartItem, String> {
    Optional<CartItem> findByAccountId(String userId);
}
