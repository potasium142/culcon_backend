package com.culcon.backend.repositories;

import com.culcon.backend.models.ProductDoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDocRepo extends JpaRepository<ProductDoc, String> {
}
