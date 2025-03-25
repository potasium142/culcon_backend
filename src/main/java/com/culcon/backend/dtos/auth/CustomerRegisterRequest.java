package com.culcon.backend.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CustomerRegisterRequest(
	@NotBlank
	@Schema(example = "sussywussy",
		requiredMode = RequiredMode.REQUIRED)
	String username,

	@NotBlank
	@Email
	@Schema(example = "example@email.com",
		requiredMode = RequiredMode.REQUIRED)
	String email,

	@NotBlank
	@Schema(example = "0123456789",
		requiredMode = RequiredMode.REQUIRED)
	@Pattern(regexp = "0[1-9]{2}[0-9]{7}")
	String phone,

	@NotBlank
	@Size(min = 6)
	@Schema(example = "sussywussy",
		requiredMode = RequiredMode.REQUIRED)
	String password,

	@Schema(example = "lussywussy",
		requiredMode = RequiredMode.REQUIRED)
	String profileName,

	@Schema(example = "69, Sussy town",
		requiredMode = RequiredMode.AUTO)
	String address,

	@Schema(example = "le sus",
		requiredMode = RequiredMode.AUTO)
	String description
) {

}
