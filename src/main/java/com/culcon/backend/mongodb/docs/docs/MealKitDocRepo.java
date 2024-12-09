package com.culcon.backend.mongodb.docs.docs;

import com.culcon.backend.mongodb.docs.MealKitDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MealKitDocRepo extends MongoRepository<MealKitDoc, String> {
}
