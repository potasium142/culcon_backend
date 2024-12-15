package com.culcon.backend.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CustomerPasswordRequest(
	@NotBlank
	@Schema(example = "sussywussy",
		requiredMode = Schema.RequiredMode.REQUIRED)
	String oldPassword,

	@NotBlank
	@Size(min = 6)
	@Schema(example = "sussywussy",
		requiredMode = Schema.RequiredMode.REQUIRED)
	String password
) {
}
