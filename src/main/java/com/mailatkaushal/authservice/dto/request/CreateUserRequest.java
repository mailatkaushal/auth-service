package com.mailatkaushal.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank @Size(min = 3, max = 20) String username,
    @NotBlank @Size(min = 3, max = 50) String password
) {}
