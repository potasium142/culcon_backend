package com.culcon.backend.repositories.record;

import org.springframework.data.jpa.repository.JpaRepository;

import com.culcon.backend.models.record.Product;

public interface ProductRepo extends JpaRepository<Product, Integer> {
}
