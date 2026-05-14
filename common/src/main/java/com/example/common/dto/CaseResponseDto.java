package com.example.common.dto;

import java.time.Instant;
import com.example.common.enums.CaseStatus;
import com.example.common.enums.CaseType;

public record CaseResponseDto(Long id, CaseType type, String description, CaseDetailsDto details, Instant createdAt,
                Instant updatedAt,
                CaseStatus status) {

}
