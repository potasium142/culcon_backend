package com.culcon.backend.models.record;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Data
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mealkit_info")
public class MealKitInfo {
	@Id
	@OneToOne
	private Product product;

	@Column(name = "ingredients")
	@JdbcTypeCode(SqlTypes.ARRAY)
	private List<Integer> ingredientsId;
}
