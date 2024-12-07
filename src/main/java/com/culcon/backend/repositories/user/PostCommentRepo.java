package com.culcon.backend.repositories.user;

import com.culcon.backend.models.user.Account;
import com.culcon.backend.models.user.PostComment;
import com.culcon.backend.models.user.PostInteractionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepo extends JpaRepository<PostComment, PostInteractionId> {
	List<PostComment> findAllByPostInteractionId_PostId(String id);

	List<PostComment> findAllByPostInteractionId_Account(Account account);
}
