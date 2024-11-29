package com.culcon.backend.repositories.docs;

import com.culcon.backend.models.docs.MealKitInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MealKitInfoDocRepo extends MongoRepository<MealKitInfo, String> {

}
