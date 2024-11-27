package com.culcon.backend.models.record;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;


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

	@Column(
		name = "product_types",
		columnDefinition = "int[]"
	)
	@JdbcTypeCode(SqlTypes.ARRAY)
	@Enumerated(EnumType.ORDINAL)
	@Builder.Default
	private List<ProductType> productTypes = new ArrayList<>();

	@Column(name = "available_quantity")
	private Integer availableQuantity;

	@Column(name = "product_status")
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Enumerated(EnumType.ORDINAL)
	private ProductStatus productStatus;

	@Column(name = "usable_duration_days")
	private Integer usableDurationDays;
}
