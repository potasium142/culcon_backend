package com.culcon.backend.models.docs;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Document(collection = "ProductInfo")
@Builder
@Getter
@Setter
@Data
public class ProductDoc {
	private String id;

	private String description;

	private HashMap<String, String> infos;

	private Set<String> tags;

	private List<String> imagesUrl;

	private Integer daysBeforeExpiry;
}
