package com.culcon.backend.repositories.docs;

import com.culcon.backend.models.docs.Blog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogDocRepo extends MongoRepository<Blog, String> {
	List<Blog> findAllByTitleContainingIgnoreCase(String keyword);
}
