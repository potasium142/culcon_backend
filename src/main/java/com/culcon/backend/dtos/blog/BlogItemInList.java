package com.culcon.backend.dtos.blog;

import com.culcon.backend.models.docs.BlogDoc;
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
	public static BlogItemInList from(BlogDoc blogDoc) {
		return BlogItemInList.builder()
			.id(blogDoc.getId())
			.title(blogDoc.getTitle())
			.description(blogDoc.getDescription())
			.imageUrl(blogDoc.getImageUrl())
			.tag(blogDoc.getTags())
			.build();
	}
}
