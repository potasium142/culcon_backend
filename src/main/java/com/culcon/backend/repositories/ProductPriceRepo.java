package com.culcon.backend.repositories;

import com.culcon.backend.models.Product;
import com.culcon.backend.models.ProductPriceHistory;
import com.culcon.backend.models.ProductPriceHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductPriceRepo extends
	JpaRepository<ProductPriceHistory, ProductPriceHistoryId> {

	Optional<ProductPriceHistory> findFirstById_ProductIdOrderById_DateDesc(String id);

	Optional<ProductPriceHistory> findFirstById_ProductOrderById_DateDesc(Product product);
}
