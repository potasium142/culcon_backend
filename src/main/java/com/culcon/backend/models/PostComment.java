package com.culcon.backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "post_comment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostComment {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(name = "comment_type")
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private CommentType commentType;

	@Column(name = "post_id", insertable = false, updatable = false)
	private String postId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Blog post;

	@Column(name = "account_id", insertable = false, updatable = false)
	private String accountId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id")
	private Account account;

	@Column(name = "parent_comment", insertable = false, updatable = false)
	@Builder.Default
	private String parentId = null;

	@ManyToOne
	@JoinColumn(name = "parent_comment")
	@Builder.Default
	private PostComment parentComment = null;

	@Column(name = "comment")
	private String comment;

	@Column(name = "status")
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Builder.Default
	private CommentStatus status = CommentStatus.NORMAL;

	@Column(name = "timestamp")
	@Builder.Default
	private Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
}
