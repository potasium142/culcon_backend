package com.culcon.backend.models.docs;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "ProductInfo")
@NoArgsConstructor
@Getter
@Setter
@Data
public class ProductDoc {
	@Id
	private String id;

	private String name = "";

	private String description = "";

	private HashMap<String, String> infos = new HashMap<>();

	private Set<String> tags = new HashSet<>();

	private List<String> imagesUrl = new ArrayList<>();

	private Integer daysBeforeExpiry = 0;

	private String articleMD = "";
}
