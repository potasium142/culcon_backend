package com.culcon.backend.repositories.record;

import com.culcon.backend.models.record.Product;
import com.culcon.backend.models.record.ProductStatus;
import com.culcon.backend.models.record.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, String> {
	List<Product> findByProductTypesAndProductStatus(
		ProductType productType, ProductStatus productStatus);
}
