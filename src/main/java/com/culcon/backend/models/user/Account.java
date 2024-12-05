package com.culcon.backend.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.Builder.Default;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "account",
	uniqueConstraints = {
		@UniqueConstraint(name = "Username", columnNames = "username"),
		@UniqueConstraint(name = "Phone", columnNames = "phone"),
		@UniqueConstraint(name = "Email", columnNames = "email")
	}
)
public class Account implements UserDetails {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.UUID)
	@Default
	private String id = "";

	@Column(name = "email", unique = true)
	@Nonnull
	@Email
	@NotBlank
	private String email;

	@Column(name = "username", unique = true)
	@Nonnull
	@NotBlank
	private String username;

	@JsonIgnore
	@Nonnull
	@NotBlank
	@Column(name = "password")
	private String password;

	@Column(name = "status")
	@Enumerated(EnumType.ORDINAL)
//	@JdbcTypeCode(SqlTypes.ENUM)
	@Default
	private AccountStatus status = AccountStatus.NORMAL;

	@Column(name = "address")
	@Default
	private String address = "";

	@Column(name = "phone", unique = true, length = 12)
	@Pattern(regexp = "0[1-9]{2}[0-9]{7}")
	@Default
	private String phone = "";

	@Column(name = "profile_pic_uri")
	@Default
	private String profilePictureUri = "defaultProfile";

	@Column(name = "profile_description")
	@Default
	private String profileDescription = "";

	@JsonIgnore
	@Column(name = "bookmarked_posts")
	@Default
	@JdbcTypeCode(SqlTypes.ARRAY)
	private Set<String> bookmarkedPost = new HashSet<>();

	@JsonIgnore
	private String token;

	@JsonIgnore
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "cart")
	@Default
	@Column(name = "amount")
	@MapKeyColumn(name = "item_id")
	private Map<String, Integer> cart = new HashMap<>();


	@JsonIgnore
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "rated_post")
	@Default
	@Column(name = "rating")
	@MapKeyColumn(name = "post_id")
	private Map<String, Integer> ratedPost = new HashMap<>();


	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("CUSTOMER"));
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return this.status != AccountStatus.BANNED;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return this.status != AccountStatus.BANNED;
	}
}
