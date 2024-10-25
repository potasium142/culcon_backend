package com.culcon.backend.dtos.auth;

import com.culcon.backend.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class StaffRegisterRequest {
    @NotBlank(message = "FullName cannot be blank")
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone cannot be blank")
    private String phone;

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotEmpty(message = "Password cannot be blank")
    private String password;

    @NotBlank(message = "Phone cannot be blank")
    private String address;

    @NotBlank(message = "SSN cannot be blank")
    private String ssn;

    @JsonIgnore
    private Role role = Role.STAFF;
}
