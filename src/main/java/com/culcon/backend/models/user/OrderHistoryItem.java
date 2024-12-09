package com.culcon.backend.models.user;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
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

	@Min(0)
	private Integer quantity;
}
