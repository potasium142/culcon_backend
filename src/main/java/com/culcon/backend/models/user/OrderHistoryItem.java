package com.culcon.backend.models.user;

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
	private ProductPriceHistory orderId;

	private Integer quantity;
}
