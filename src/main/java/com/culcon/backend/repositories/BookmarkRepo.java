package com.culcon.backend.repositories;

import com.culcon.backend.models.Bookmark;
import com.culcon.backend.models.BookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepo extends JpaRepository<Bookmark, String> {
	List<Bookmark> findAllById_Account_Id(String idAccountId);

	Boolean existsById(BookmarkId id);
}
