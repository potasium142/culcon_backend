package com.culcon.backend.models;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
public class BookmarkId {
	@OneToOne
	private Account account;

	@ManyToOne
	@JoinColumn(name = "blog_id")
	private Blog blog;
}
