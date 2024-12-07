package com.culcon.backend.repositories.user;

import com.culcon.backend.models.user.Product;
import com.culcon.backend.models.user.ProductPriceHistory;
import com.culcon.backend.models.user.ProductPriceHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductPriceRepo extends
	JpaRepository<ProductPriceHistory, ProductPriceHistoryId> {

	Optional<ProductPriceHistory> findFirstById_ProductIdOrderById_DateDesc(String id);

	Optional<ProductPriceHistory> findFirstById_ProductOrderById_DateDesc(Product product);
}
