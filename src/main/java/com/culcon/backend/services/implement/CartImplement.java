package com.culcon.backend.services.implement;

import com.culcon.backend.models.user.CartItem;
import com.culcon.backend.repositories.user.AccountRepo;
import com.culcon.backend.repositories.user.CartRepo;
import com.culcon.backend.services.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartImplement implements CartService {
    private final CartRepo cartRepo;
    private final AccountRepo userRepo;

    @Override
    public List<CartItem> getUserCartItems(HttpServletRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
