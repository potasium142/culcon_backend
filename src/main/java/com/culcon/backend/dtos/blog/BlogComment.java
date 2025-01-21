package com.culcon.backend.dtos.blog;

import com.culcon.backend.models.PostComment;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record BlogComment(
	String id,
	String accountName,
	String profilePicture,
	Timestamp timestamp,
	String comment
) {
	public static BlogComment from(PostComment comment) {
		return BlogComment.builder()
			.id(comment.getId())
			.comment(comment.getComment())
			.accountName(comment.getAccount().getUsername())
			.profilePicture(comment.getAccount().getProfilePictureUri())
			.timestamp(comment.getTimestamp())
			.build();
	}
}
