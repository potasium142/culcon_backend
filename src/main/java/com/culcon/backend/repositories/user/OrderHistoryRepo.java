package com.culcon.backend.repositories.user;

import com.culcon.backend.models.user.Account;
import com.culcon.backend.models.user.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHistoryRepo extends JpaRepository<OrderHistory, Long> {
	List<OrderHistory> findByUser(Account user);
}
