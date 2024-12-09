package com.culcon.backend.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "post_comment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostComment {
	@EmbeddedId
	private PostInteractionId postInteractionId;

	private String comment;
}
