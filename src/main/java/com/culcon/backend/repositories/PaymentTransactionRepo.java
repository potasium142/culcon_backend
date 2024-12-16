package com.culcon.backend.repositories;

import com.culcon.backend.models.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction, String> {
	Optional<PaymentTransaction> findByTransactionId(String transactionId);
}
