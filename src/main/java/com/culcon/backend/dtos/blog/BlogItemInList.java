package com.culcon.backend.dtos.blog;

import com.culcon.backend.models.Blog;
import lombok.Builder;

@Builder
public record BlogItemInList(
	String id,
	String title,
	String description,
	String imageUrl
) {
	public static BlogItemInList from(Blog blogDoc) {
		return BlogItemInList.builder()
			.id(blogDoc.getId())
			.title(blogDoc.getTitle())
			.description(blogDoc.getDescription())
			.imageUrl(blogDoc.getThumbnail())
			.build();
	}
}
