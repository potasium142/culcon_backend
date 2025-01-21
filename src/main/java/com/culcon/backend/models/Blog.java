package com.culcon.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Data
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "blog")
@Immutable
public class Blog {
	@Id
	private String id;

	@Column(name = "title")
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "article")
	private String article;

	@Column(name = "thumbnail")
	private String thumbnail;

	@Column(name = "infos")
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, String> infos;
}
