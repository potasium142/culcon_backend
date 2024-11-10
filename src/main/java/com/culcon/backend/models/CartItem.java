package com.culcon.backend.models;


import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
	private Integer itemId;
	private Integer quantity;
}
