package com.culcon.backend.models;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "mealkit_ingredients")
public class MealkitIngredients {
	@EmbeddedId
	private MealkitIngredientsId id;
}
