package com.culcon.backend.dtos.auth;

import com.culcon.backend.models.user.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CustomerPasswordRequest(
        @NotBlank
        @Schema(example = "sussywussy",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String oldPassword,

        @NotBlank
        @Schema(example = "sussywussy",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
    public static Account mapToAccount(
            CustomerPasswordRequest reqBody) {
        return Account.builder()
                .password(reqBody.password)
                .build();
    }
}
