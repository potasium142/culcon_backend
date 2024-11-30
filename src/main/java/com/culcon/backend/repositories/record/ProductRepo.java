package com.culcon.backend.repositories.record;

import com.culcon.backend.models.record.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, String> {
}
