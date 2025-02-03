package com.culcon.backend.dtos.blog;

import com.culcon.backend.models.CommentType;
import com.culcon.backend.models.PostComment;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record UserCommentList(
	String id,
	String postId,
	Timestamp timestamp,
	String comment,
	CommentType commentType,
	Boolean deleted
) {
	public static UserCommentList from(PostComment postComment) {
		return UserCommentList.builder()
			.id(postComment.getId())
			.postId(postComment.getPostId())
			.timestamp(postComment.getTimestamp())
			.comment(postComment.getComment())
			.commentType(postComment.getCommentType())
			.deleted(postComment.isDeleted())
			.build();
	}
}
