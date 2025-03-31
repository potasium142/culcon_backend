package com.culcon.backend.dtos.blog;

import com.culcon.backend.models.PostComment;
import lombok.Builder;

@Builder
public record BlogCommentWithReply(
	BlogComment comment,
	Integer replyAmount,
	BlogComment firstReply
) {
	public static BlogCommentWithReply of(
		PostComment comment, Integer replyAmount, PostComment firstReply) {
		var mainComment = BlogComment.from(comment);

		if (firstReply == null) {
			return new BlogCommentWithReply(mainComment, replyAmount, null);
		}

		var mainReply = BlogComment.from(firstReply);

		return new BlogCommentWithReply(mainComment, replyAmount, mainReply);
	}
}
