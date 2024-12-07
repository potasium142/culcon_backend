package com.culcon.backend.repositories.docs;

import com.culcon.backend.models.docs.BlogDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogDocRepo extends MongoRepository<BlogDoc, String> {
	List<BlogDoc> findAllByTitleContainingIgnoreCase(String keyword);
}
