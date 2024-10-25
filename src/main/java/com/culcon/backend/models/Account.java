package com.culcon.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;
import lombok.*;
import lombok.Builder.Default;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Getter
@Setter
@Table(name = "account")
public class Account implements UserDetails {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "email", unique = true)
    @Nonnull
    private String email;

    @Column(name = "username", unique = true)
    @Nonnull
    private String username;

    @JsonIgnore
    @Nonnull
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role")
    private Role role;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    @Default
    private AccountStatus status = AccountStatus.NORMAL;

    @JsonIgnore
    private String token;

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
