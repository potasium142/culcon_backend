package com.culcon.backend.mongodb.docs.docs;

import com.culcon.backend.mongodb.docs.ProductDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductDocRepo extends MongoRepository<ProductDoc, String> {
}
