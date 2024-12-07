package com.culcon.backend.dtos.blog;

import com.culcon.backend.models.docs.Blog;
import lombok.Builder;

import java.util.Set;

@Builder
public record BlogItemInList(
	String id,
	String title,
	String description,
	String imageUrl,
	Set<String> tag
) {
	public static BlogItemInList from(Blog blog) {
		return BlogItemInList.builder()
			.id(blog.getId())
			.title(blog.getTitle())
			.description(blog.getDescription())
			.imageUrl(blog.getImageUrl())
			.tag(blog.getTags())
			.build();
	}
}
