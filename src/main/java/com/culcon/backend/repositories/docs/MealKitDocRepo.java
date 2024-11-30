package com.culcon.backend.repositories.docs;

import com.culcon.backend.models.docs.MealKitDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MealKitDocRepo extends MongoRepository<MealKitDoc, String> {
}
