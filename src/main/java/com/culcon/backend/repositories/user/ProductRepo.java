package com.culcon.backend.repositories.user;

import com.culcon.backend.models.user.Product;
import com.culcon.backend.models.user.ProductStatus;
import com.culcon.backend.models.user.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, String> {
	List<Product> findByProductTypesAndProductStatus(
		ProductType productType, ProductStatus productStatus);

	List<Product> findAllByProductStatus(ProductStatus productStatus);

}
