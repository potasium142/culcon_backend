package com.culcon.backend.models;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderHistoryItem {
	@ManyToOne
	private ProductPriceHistory productId;

	private Integer quantity;
}
