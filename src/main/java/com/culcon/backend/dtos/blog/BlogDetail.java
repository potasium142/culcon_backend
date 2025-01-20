package com.culcon.backend.dtos.blog;

import com.culcon.backend.models.Blog;
import lombok.Builder;

@Builder
public record BlogDetail(
	Blog blog,
	Boolean bookmark
) {
}
