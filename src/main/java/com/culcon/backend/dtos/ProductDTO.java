package com.culcon.backend.dtos;

import com.culcon.backend.mongodb.docs.ProductDoc;
import com.culcon.backend.models.Product;
import com.culcon.backend.models.ProductStatus;
import com.culcon.backend.models.ProductType;
import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Builder
public record ProductDTO(

	String id,

	String name,

	ProductType productTypes,

	Integer availableQuantity,

	ProductStatus productStatus,

	String description,

	HashMap<String, String> infos,

	Set<String> tags,

	List<String> imagesUrl,

	Integer daysBeforeExpiry,

	String articleMD,

	Float price,

	Float salePercent
) {
	public static ProductDTO from(
		Product product,
		ProductDoc productDoc
	) {
		return ProductDTO.builder()
			.id(product.getId())
			.name(product.getProductName())
			.productTypes(product.getProductTypes())
			.availableQuantity(product.getAvailableQuantity())
			.productStatus(product.getProductStatus())
			.description(productDoc.getDescription())
			.infos(productDoc.getInfos())
			.tags(productDoc.getTags())
			.imagesUrl(productDoc.getImagesUrl())
			.daysBeforeExpiry(productDoc.getDaysBeforeExpiry())
			.articleMD(productDoc.getArticleMD())
			.price(product.getPrice())
			.salePercent(product.getSalePercent())
			.build();
	}
}
