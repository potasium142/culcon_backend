package com.culcon.backend.dtos.blog;

import com.culcon.backend.models.user.PostComment;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record BlogComment(
	String accountName,
	String profilePicture,
	Timestamp timestamp,
	String comment
) {
	public static BlogComment from(PostComment comment) {
		var account = comment.getPostInteractionId().getAccount();
		return BlogComment.builder()
			.comment(comment.getComment())
			.accountName(account.getUsername())
			.profilePicture(account.getProfilePictureUri())
			.timestamp(comment.getPostInteractionId().getTimestamp())
			.build();
	}
}
