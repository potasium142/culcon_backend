package com.culcon.backend.dtos;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record CloudinaryImageDTO(
	String folder,
	String url,
	String displayName,
	List<Object> tags
) {
	public static Map toMap(CloudinaryImageDTO dto) {
		return Map.of(
			"folder", dto.folder(),
			"url", dto.url(),
			"displayName", dto.displayName(),
			"tags", dto.tags()
		);
	}

	public static CloudinaryImageDTO from(Map map) {
		return CloudinaryImageDTO.builder()
			.folder((String) map.get("asset_folder"))
			.url((String) map.get("url"))
			.displayName((String) map.get("display_name"))
			.tags((List<Object>) map.get("tags"))
			.build();
	}
}
