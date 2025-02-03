package com.culcon.backend.repositories;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.CommentType;
import com.culcon.backend.models.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepo extends JpaRepository<PostComment, String> {
	List<PostComment> findAllByPostIdAndCommentType(String postId, CommentType commentType);

	List<PostComment> findAllByPostIdAndParentComment_Id(String postId, String parentCommentId);

	List<PostComment> findAllByAccount(Account account);

	Optional<PostComment> findByIdAndAccount(String id, Account account);
}
