package com.culcon.backend.dtos.blog;

import com.culcon.backend.models.CommentStatus;
import com.culcon.backend.models.PostComment;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record BlogComment(
	String id,
	String accountName,
	String profilePicture,
	Timestamp timestamp,
	String comment,
	CommentStatus status
) {
	public static BlogComment from(PostComment comment) {
		if (comment.getStatus() == CommentStatus.DELETED) {
			return BlogComment.builder()
				.id(comment.getId())
				.comment("This comment has been deleted")
				.accountName("Anonymous")
				.profilePicture("defaultProfile")
				.timestamp(comment.getTimestamp())
				.status(comment.getStatus())
				.build();
		}
		return BlogComment.builder()
			.id(comment.getId())
			.comment(comment.getComment())
			.accountName(comment.getAccount().getUsername())
			.profilePicture(comment.getAccount().getProfilePictureUri())
			.timestamp(comment.getTimestamp())
			.status(comment.getStatus())
			.build();
	}
}
