package com.culcon.backend.dtos.order;

import com.culcon.backend.models.OrderHistoryItem;
import com.culcon.backend.models.ProductType;
import lombok.Builder;

@Builder
public record OrderItem(
	Float price,
	String id,
	String name,
	ProductType productType,
	Integer quantity,
	String imageUrl
) {
	public static OrderItem from(OrderHistoryItem item) {
		var product = item.getProductId().getId().getProduct();
		return OrderItem.builder()
			.id(product.getId())
			.price(item.getProductId().getPrice())
			.name(product.getProductName())
			.imageUrl(product.getImageUrl())
			.productType(product.getProductTypes())
			.quantity(item.getQuantity())
			.build();
	}
}
