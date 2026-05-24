package com.example.common.dto;

import java.util.List;

public record ChatResponseDto(Long id, List<ChatParticipantResponseDto> participants,
        List<ChatMessageResponseDto> messages) {

}
