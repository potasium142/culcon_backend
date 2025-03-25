package com.culcon.backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
@Embeddable
@AttributeOverrides({
	@AttributeOverride(
		name = "product", column = @Column(name = "product_id")
	),
	@AttributeOverride(
		name = "date", column = @Column(name = "date")
	)
})
public class ProductPriceHistoryId {
	@ManyToOne
	private Product product;

	@JdbcTypeCode(SqlTypes.LOCAL_DATE_TIME)
	private LocalDateTime date;
}
