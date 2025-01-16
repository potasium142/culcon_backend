package com.culcon.backend.mongodb.model;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;

@Document(collection = "ProductInfo")
@NoArgsConstructor
@Getter
@Setter
@Data
public class ProductDoc {
	@Id
	private String id;

	private String name;

	private String description;

	private HashMap<String, String> infos;

	private List<String> images_url;

	private Integer day_before_expiry;

	private String article_md;
}
