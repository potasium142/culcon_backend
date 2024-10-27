package com.culcon.backend.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StaffRegisterRequest(
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
        @Pattern(regexp = "(84|0)[1-9][0-9]{1,9}")
        String phone,

        @NotBlank
        @Schema(example = "sussywussy",
                requiredMode = RequiredMode.REQUIRED)
        String password,

        @NotBlank
        @Schema(example = "0123445678901234",
                requiredMode = RequiredMode.REQUIRED)
        String ssn,

        @NotBlank
        @Schema(example = "69, Sussy town",
                requiredMode = RequiredMode.AUTO)
        String address) {
}
