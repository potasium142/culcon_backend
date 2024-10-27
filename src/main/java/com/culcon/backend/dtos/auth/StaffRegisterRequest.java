package com.culcon.backend.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StaffRegisterRequest(
        @NotBlank String username,

        @NotBlank @Email String email,

        @NotBlank @Pattern(regexp = "(84|0)[1-9][0-9]{1,9}") String phone,

        @NotBlank String ssn,

        @NotBlank String password,

        @NotBlank String address) {
}
