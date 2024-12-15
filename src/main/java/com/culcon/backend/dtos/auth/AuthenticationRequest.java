package com.culcon.backend.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthenticationRequest(
	@NotBlank(message = "Username cannot be blank")
	String username,
	@NotBlank(message = "Password cannot be blank")
	String password
) {
}
