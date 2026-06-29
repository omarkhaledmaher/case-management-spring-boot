package com.example.common.dto;

import java.time.Instant;

public record EventResponseDto(Long id, EventCodeDto code, Instant timestamp, String methodName, String username,
        String response) {

}
