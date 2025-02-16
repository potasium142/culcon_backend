package com.culcon.backend.repositories;

import com.culcon.backend.models.MealkitIngredients;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealkitIngredientsRepo extends JpaRepository<MealkitIngredients, String> {
	List<MealkitIngredients> findAllById_Mealkit_Id(String id);

	List<MealkitIngredients> findAllById_Ingredient_Id(String idIngredientId);
}
