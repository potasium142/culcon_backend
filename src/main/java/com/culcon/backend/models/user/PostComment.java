package com.culcon.backend.models.user;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "post_comment")
public class PostComment {
	@EmbeddedId
	private PostInteractionId postInteractionId;

	private String comment;
}
