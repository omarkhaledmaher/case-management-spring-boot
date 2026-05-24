package com.example.common.dto;

import java.time.Instant;

public record ChatMessageResponseDto(Long id, ChatParticipantResponseDto sender, String text, Instant timestamp) {

}
