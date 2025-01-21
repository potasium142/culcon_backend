package com.culcon.backend.repositories;

import com.culcon.backend.models.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepo extends JpaRepository<Blog, String> {
	List<Blog> findAllByTitleContainingIgnoreCase(String title);
}
