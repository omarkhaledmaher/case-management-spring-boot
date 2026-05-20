package com.example.common.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequestDto(@NotBlank(message = "Message cannot be blank") String text) {

}
