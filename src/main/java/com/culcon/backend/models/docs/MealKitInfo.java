package com.culcon.backend.models.docs;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "MealKitInfo")
@Builder
@Getter
@Setter
@Data
public class MealKitInfo {
	private String id;
}
