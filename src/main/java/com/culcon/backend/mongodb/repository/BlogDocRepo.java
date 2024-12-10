package com.culcon.backend.mongodb.repository;

import com.culcon.backend.mongodb.model.BlogDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogDocRepo extends MongoRepository<BlogDoc, String> {
	List<BlogDoc> findAllByTitleContainingIgnoreCase(String keyword);
}
