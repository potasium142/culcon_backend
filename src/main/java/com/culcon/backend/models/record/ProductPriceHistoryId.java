package com.culcon.backend.models.record;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductPriceHistoryId {
	@ManyToOne
	private Product product;

	@JdbcTypeCode(SqlTypes.LOCAL_DATE_TIME)
	private LocalDateTime date;

}
