package com.culcon.backend.mongodb.repository;

import com.culcon.backend.mongodb.model.ProductDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductDocRepo extends MongoRepository<ProductDoc, String> {
}
