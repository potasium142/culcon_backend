package com.culcon.backend.models.record;

import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "product_name")
	private String productName;

	@Column(name = "product_types")
	@Enumerated(EnumType.ORDINAL)
	private ProductType productTypes;

	@Column(name = "available_quantity")
	private Integer availableQuantity;

	@Column(name = "product_status")
	@Enumerated(EnumType.ORDINAL)
	private ProductStatus productStatus;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "price")
	private Float price;

	@Column(name = "sale_percent")
	private Float salePercent;
}
