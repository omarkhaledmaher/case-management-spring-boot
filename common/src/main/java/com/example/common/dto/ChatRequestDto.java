package com.example.common.dto;

import java.util.List;
import jakarta.validation.constraints.Size;

public record ChatRequestDto(
        @Size(min = 2, message = "At least two users must be selected") List<Long> participantIds) {

}
