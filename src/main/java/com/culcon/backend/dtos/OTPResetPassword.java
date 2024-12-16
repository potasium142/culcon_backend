package com.culcon.backend.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record OTPResetPassword(
	@NotNull
	String id,
	@NotNull
	String otp,
	@NotNull
	@Size(min = 6)
	String password
) {
}
