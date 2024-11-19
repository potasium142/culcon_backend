package com.culcon.backend.dtos.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthenticationResponse(
	@JsonProperty("accessToken")
	String accessToken
) {
}
