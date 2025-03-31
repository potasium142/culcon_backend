package com.culcon.backend.repositories;

import com.culcon.backend.models.Product;
import com.culcon.backend.models.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, String> {

//	List<Product> findAllByProductStatus(ProductStatus productStatus);

	List<Product> findAllByProductNameContainingIgnoreCaseAndProductTypes(String name, ProductType productType);

	List<Product> findAllByProductNameContainingIgnoreCase(String name);

	Page<Product> findAllByProductTypes(ProductType productType, Pageable pageable);

}
