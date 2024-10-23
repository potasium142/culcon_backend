package com.culcon.backend.dtos.auth;

import com.culcon.backend.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerRegisterRequest {
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

  @JsonIgnore private Role role = Role.CUSTOMER;
}
