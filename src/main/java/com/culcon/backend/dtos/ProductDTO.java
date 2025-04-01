package com.culcon.backend.dtos;

import com.culcon.backend.models.*;
import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public record ProductDTO(

	String id,

	String name,

	ProductType productTypes,

	Integer availableQuantity,

	ProductStatus productStatus,

	String description,

	HashMap<String, String> infos,

	List<String> imagesUrl,

	Integer daysBeforeExpiry,

	String articleMD,

	Float price,

	Float salePercent,

	Map<String, Integer> ingredients,

	List<String> instructions
) {
	public static ProductDTO from(
		Product product,
		ProductDoc productDoc,
		List<MealkitIngredients> ingredients
	) {
		var ingredientsMap = new HashMap<String, Integer>();

		for (MealkitIngredients i : ingredients) {
			ingredientsMap.put(i.getId().getIngredient().getId(), i.getAmount());
		}

		return ProductDTO.builder()
			.id(product.getId())
			.name(product.getProductName())
			.productTypes(product.getProductTypes())
			.availableQuantity(product.getAvailableQuantity())
			.productStatus(product.getProductStatus())
			.description(productDoc.getDescription())
			.infos(productDoc.getInfos())
			.imagesUrl(productDoc.getImagesUrl())
			.daysBeforeExpiry(productDoc.getDayBeforeExpiry())
			.articleMD(productDoc.getArticle())
			.price(product.getPrice())
			.salePercent(product.getSalePercent())
			.ingredients(ingredientsMap)
			.instructions(productDoc.getInstructions())
			.build();
	}
}
