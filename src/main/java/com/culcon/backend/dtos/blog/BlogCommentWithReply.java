package com.culcon.backend.dtos.blog;

public record BlogCommentPost(
	BlogComment comment,
	Integer replyAmount,
	BlogComment firstReply
) {
}
