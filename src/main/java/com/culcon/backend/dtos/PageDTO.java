package com.culcon.backend.dtos;

import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record PageDTO<T>(
	T content,
	Integer totalPage,
	Integer pageIndex,
	Long totalElement
) {
	public static PageDTO<?> of(Object content, Page<?> page) {
		return PageDTO.builder()
			.content(content)
			.totalPage(page.getTotalPages())
			.pageIndex(page.getNumber())
			.totalElement(page.getTotalElements())
			.build();
	}

	public static PageDTO<?> of(Page<?> page) {
		return PageDTO.builder()
			.content(page.getContent())
			.totalPage(page.getTotalPages())
			.pageIndex(page.getNumber())
			.totalElement(page.getTotalElements())
			.build();
	}
}
