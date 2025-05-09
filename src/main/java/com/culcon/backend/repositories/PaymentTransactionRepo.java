package com.culcon.backend.repositories;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.OrderHistory;
import com.culcon.backend.models.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction, Long> {
	Optional<PaymentTransaction> findByTransactionId(String transactionId);

	Optional<PaymentTransaction> findByOrder(OrderHistory order);

	Optional<PaymentTransaction> findByIdAndOrder_User(String id, Account orderUser);


}
