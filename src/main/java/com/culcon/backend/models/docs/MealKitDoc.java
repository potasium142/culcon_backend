package com.culcon.backend.models.docs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "MealKitInfo")
@Getter
@Setter
@Data
public class MealKitDoc extends ProductDoc {
	private List<String> instructions = new ArrayList<>();

	private List<String> ingredients = new ArrayList<>();
}
