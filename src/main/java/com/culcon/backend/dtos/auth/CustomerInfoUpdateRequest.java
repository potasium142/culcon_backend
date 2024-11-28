package com.culcon.backend.dtos.auth;

import com.culcon.backend.models.user.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
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
        @Email
        @Schema(example = "example@email.com",
                requiredMode = RequiredMode.REQUIRED)
        String email,

        @NotBlank
        @Schema(example = "0123456789",
                requiredMode = RequiredMode.REQUIRED)
        @Pattern(regexp = "(84|0)[1-9][0-9]{1,9}")
        String phone,


        @Schema(example = "69, Sussy town",
                requiredMode = RequiredMode.AUTO)
        String address,

        @Schema(example = "le sus",
                requiredMode = RequiredMode.AUTO)
        String description
) {
    public static Account mapToAccount(
            CustomerInfoUpdateRequest reqBody) {
        return Account.builder()
                .username(reqBody.username)
                .address(reqBody.address)
                .phone(reqBody.phone)
                .email(reqBody.email)
                .profileDescription(reqBody.description)
                .build();
    }

}
