package com.example.common.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(@NotBlank(message = "Username cannot be blank") String username,
                @NotBlank(message = "Password cannot be blank") String password) {

}
