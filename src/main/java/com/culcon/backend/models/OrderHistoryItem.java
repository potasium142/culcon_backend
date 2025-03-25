package com.culcon.backend.models;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Data
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderHistoryItem {
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "product_id", referencedColumnName = "product_id"),
		@JoinColumn(name = "date", referencedColumnName = "date")
	})
	private ProductPriceHistory productId;

	@Min(0)
	private Integer quantity;
}
