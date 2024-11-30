package com.culcon.backend.repositories.docs;

import com.culcon.backend.models.docs.ProductDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductDocRepo extends MongoRepository<ProductDoc, String> {
}
