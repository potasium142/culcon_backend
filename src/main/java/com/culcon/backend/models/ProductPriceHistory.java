package com.culcon.backend.models;


import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Immutable;

@Data
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@Table(name = "product_price_history")
public class ProductPriceHistory {
	@EmbeddedId
	private ProductPriceHistoryId id;

	@Column(name = "price")
	private Float price;

	@Column(name = "sale_percent")
	private Float salePercent;
}
