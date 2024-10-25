package com.culcon.backend.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CustomerRegisterRequest(
        @NotBlank(message = "username cannot be blank") String username,

        @NotBlank(message = "email cannot be blank") @Email String email,

        @NotBlank(message = "phone cannot be blank") String phone,

        @NotEmpty(message = "password cannot be blank") String password,

        @NotBlank(message = "address cannot be blank") String address) {
}
