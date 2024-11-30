package com.culcon.backend.models.record;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;


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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "product_name")
	private String productName;

	@Column(name = "product_types")
	@Enumerated(EnumType.ORDINAL)
	private ProductType productTypes;

	@Column(name = "available_quantity")
	private Integer availableQuantity;

	@Column(name = "product_status")
//	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Enumerated(EnumType.ORDINAL)
	private ProductStatus productStatus;

	@Column(name = "days_before_expiry")
	private Integer daysBeforeExpiry;

	@Column(name = "tags")
	@JdbcTypeCode(SqlTypes.ARRAY)
	private HashSet<String> tags;
}
