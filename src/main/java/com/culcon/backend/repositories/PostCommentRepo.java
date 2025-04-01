package com.culcon.backend.repositories;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepo extends JpaRepository<PostComment, String> {

	List<PostComment> findAllByAccount(Account account);

	Optional<PostComment> findByIdAndAccount(String id, Account account);

	Integer countByParentComment_IdAndPostId(String parentComment_id, String postId);

	PostComment findFirstByPostIdAndParentComment_IdOrderByTimestampDesc(
		String postId, String parentComment_id);

	Page<PostComment> findAllByPostIdAndParentComment_IdOrderByTimestampDesc(
		String postId, String parentComment_id, Pageable pageable);
}
