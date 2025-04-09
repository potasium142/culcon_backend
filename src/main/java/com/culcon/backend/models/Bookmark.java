package com.culcon.backend.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder()
@Table(name = "bookmark")
public class Bookmark {
	@EmbeddedId
	private BookmarkId id;
}
