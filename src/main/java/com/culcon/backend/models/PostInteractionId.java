package com.culcon.backend.models;


import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostInteractionId {
	private String postId;

	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;

	private Timestamp timestamp;
}
