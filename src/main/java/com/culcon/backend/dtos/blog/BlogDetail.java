package com.culcon.backend.dtos.blog;

import com.culcon.backend.mongodb.docs.BlogDoc;
import lombok.Builder;

@Builder
public record BlogDetail(
	BlogDoc blog,
	Boolean bookmark
) {
}
