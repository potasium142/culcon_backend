package com.culcon.backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.List;

@Data
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@Table(name = "product_doc")
public class ProductDoc {
	@Id
	@Column(name = "id")
	private String id;

	@OneToOne
	@JoinColumn(name = "id")
	private Product product;

	@Column(name = "description")
	private String description;

	@Column(name = "images_url")
	@JdbcTypeCode(SqlTypes.ARRAY)
	private List<String> imagesUrl;

	@Column(name = "infos")
	@JdbcTypeCode(SqlTypes.JSON)
	private HashMap<String, String> infos;

	@Column(name = "instructions")
	@JdbcTypeCode(SqlTypes.ARRAY)
	private List<String> instructions;

	@Column(name = "article_md")
	private String article;

	@Column(name = "day_before_expiry")
	private Integer dayBeforeExpiry;
}
