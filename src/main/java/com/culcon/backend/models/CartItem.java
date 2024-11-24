package com.culcon.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_items") // Ensure table name matches your database schema
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "cart_id", updatable = false, nullable = false)
	private String cartId;

	@Column(name = "account_id")
	@NotNull
	@Email
	@NotBlank
	private String accountId;

	@Column(name = "item_id")
	@NotNull
	private Integer itemId;

	@Column(name = "quantity")
	@NotNull
	private Integer quantity;
}
