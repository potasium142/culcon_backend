package com.culcon.backend.repositories;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.OrderHistory;
import com.culcon.backend.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderHistoryRepo extends JpaRepository<OrderHistory, Long> {
	List<OrderHistory> findByUser(Account user);

	List<OrderHistory> findByUserAndOrderStatus(Account user, OrderStatus orderStatus);

	Optional<OrderHistory> findByIdAndUser(Long id, Account user);
}
