package com.example.common.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(@NotBlank(message = "Username cannot be blank") String username,
                @NotBlank(message = "Password cannot be blank") String password,
                List<String> roleNames) {
}
