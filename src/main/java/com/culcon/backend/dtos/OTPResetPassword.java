package com.culcon.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OTPResetPassword(
	@NotNull
	String id,
	@NotNull
	String otp,
	@NotNull
	String password
) {
}
