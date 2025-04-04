package com.culcon.backend.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record CustomerInfoUpdateRequest(
	@NotBlank
	@Schema(example = "sussywussy",
		requiredMode = RequiredMode.REQUIRED)
	String username,


	@NotBlank
	@Schema(example = "0123456789",
		requiredMode = RequiredMode.REQUIRED)
	@Pattern(regexp = "0[1-9][0-9]{8,9}")
	String phone,

	@Schema(example = "lussymussy",
		requiredMode = RequiredMode.AUTO)
	String profileName,

	@Schema(example = "69, Sussy town",
		requiredMode = RequiredMode.AUTO)
	String address,

	@Schema(example = "le sus",
		requiredMode = RequiredMode.AUTO)
	String description
) {

}
