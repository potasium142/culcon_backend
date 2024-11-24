package com.culcon.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.Builder.Default;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "role")
	@Nonnull
	@Default
	private Role role = Role.CUSTOMER;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "status")
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
	private String token;

	@JsonIgnore
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "cart")
	@Default
	private List<CartItem> cart = new ArrayList<>();


	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
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
