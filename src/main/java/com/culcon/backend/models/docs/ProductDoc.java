package com.culcon.backend.models.docs;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "ProductInfo")
@Builder
@Getter
@Setter
@Data
public class ProductDoc {
	@Id
	private String id;

	@Builder.Default
	private String name = "";

	@Builder.Default
	private String description = "";

	@Builder.Default
	private HashMap<String, String> infos = new HashMap<>();

	@Builder.Default
	private Set<String> tags = new HashSet<>();

	@Builder.Default
	private List<String> imagesUrl = new ArrayList<>();

	@Builder.Default
	private Integer daysBeforeExpiry = 0;

	@Builder.Default
	private String articleMD = "";
}
