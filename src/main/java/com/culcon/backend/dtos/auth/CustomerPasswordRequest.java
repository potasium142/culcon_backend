package com.culcon.backend.dtos.auth;

import com.culcon.backend.models.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record CustomerPasswordRequest
(
        @NotBlank
        @Schema(example = "sussywussy",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String oldpassword,
    @NotBlank
    @Schema(example = "sussywussy",
            requiredMode = Schema.RequiredMode.REQUIRED)
    String password,

    @NotBlank
    @Schema(example = "sussywussy",
            requiredMode = Schema.RequiredMode.REQUIRED)
            String repassword
)
    {

        public static Account mapToAccount(
                CustomerPasswordRequest reqBody) {
            return Account.builder()

                    .password(reqBody.password)

                    .build();
        }
}
