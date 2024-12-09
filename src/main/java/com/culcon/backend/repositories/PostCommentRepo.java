package com.culcon.backend.repositories;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.PostComment;
import com.culcon.backend.models.PostInteractionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepo extends JpaRepository<PostComment, PostInteractionId> {
	List<PostComment> findAllByPostInteractionId_PostId(String id);

	List<PostComment> findAllByPostInteractionId_Account(Account account);
}
