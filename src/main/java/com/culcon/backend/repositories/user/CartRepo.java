package com.culcon.backend.repositories.user;

import com.culcon.backend.models.user.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepo extends JpaRepository<CartItem, String> {
    Optional<CartItem> findByAccountId(String userId);
}
