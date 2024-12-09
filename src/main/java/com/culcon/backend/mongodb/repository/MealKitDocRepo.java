package com.culcon.backend.mongodb.repository;

import com.culcon.backend.mongodb.model.MealKitDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MealKitDocRepo extends MongoRepository<MealKitDoc, String> {
}
