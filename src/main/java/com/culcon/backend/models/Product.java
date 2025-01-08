package com.culcon.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


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
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private ProductType productTypes;

	@Column(name = "available_quantity")
	@Min(0)
	private Integer availableQuantity;

	@Column(name = "product_status")
	@Enumerated(EnumType.ORDINAL)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private ProductStatus productStatus;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "price")
	private Float price;

	@Column(name = "sale_percent")
	private Float salePercent;
}
