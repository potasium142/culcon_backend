package com.culcon.backend.models.docs;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Data
@Builder
@Document(collection = "Blog")
public class Blog {
	@Id
	String id;

	String title;

	String description;

	String markdownText;
}
