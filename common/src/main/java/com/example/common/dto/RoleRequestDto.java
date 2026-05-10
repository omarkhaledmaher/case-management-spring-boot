package com.example.common.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;

public record RoleRequestDto(@NotBlank(message = "Name cannot be blank") String name, List<String> privileges) {

}
