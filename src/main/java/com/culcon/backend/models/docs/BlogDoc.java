package com.culcon.backend.models.docs;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Data
@Builder
@Document(collection = "Blog")
public class BlogDoc {
	@Id
	String id;

	String title;

	String description;

	String markdownText;

	Map<String, String> infos;

	Set<String> tags;

	Set<String> relatedProduct;

	String imageUrl;
}
