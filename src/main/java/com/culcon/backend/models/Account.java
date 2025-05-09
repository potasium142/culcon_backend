package com.culcon.backend.models;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "user_account",
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
	@Pattern(regexp = "[A-Za-z0-9_-]+")
	private String username;


	@Column(name = "profile_name")
	@Default
	private String profileName = "";


	@JsonIgnore
	@Nonnull
	@NotBlank
	@Column(name = "password")
	private String password;

	@Column(name = "status")
	@Default
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private AccountStatus status = AccountStatus.NORMAL;

	@Column(name = "address")
	@Default
	private String address = "";

	@Column(name = "phone", unique = true, length = 12)
	@Default
	@Pattern(regexp = "0[1-9][0-9]{8,9}")
	private String phone = "";

	@Column(name = "profile_pic_uri")
	@Default
	private String profilePictureUri = "defaultProfile";

	@Column(name = "profile_description")
	@Default
	private String profileDescription = "";

	@JsonIgnore
	@Column(name = "online_status")
	@Default
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private OnlineStatus onlineStatus = OnlineStatus.OFFLINE;

	@JsonIgnore
	@Builder.Default
	private String token = "";

	@JsonIgnore
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "cart")
	@Default
	@Column(name = "amount")
	@MapKeyJoinColumn(name = "product_id")
	private Map<Product, Integer> cart = new HashMap<>();

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
