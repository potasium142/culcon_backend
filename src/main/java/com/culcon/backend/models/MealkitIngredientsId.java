package com.culcon.backend.models;


import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MealkitIngredientsId {
	@OneToOne
	private Product mealkit;

	@ManyToOne
	@JoinColumn(name = "ingredient")
	private Product ingredient;
}
