package com.culcon.backend.repositories.docs;

import com.culcon.backend.models.docs.Blog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlogDocRepo extends MongoRepository<Blog, String> {
}
