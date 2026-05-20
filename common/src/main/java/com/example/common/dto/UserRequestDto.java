package com.example.common.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record UserRequestDto(@NotBlank(message = "Username cannot be blank") String username,
        @NotBlank(message = "Password cannot be blank") String password,
        @NotEmpty(message = "At least one role must be assigned") List<String> roleNames) {
}
