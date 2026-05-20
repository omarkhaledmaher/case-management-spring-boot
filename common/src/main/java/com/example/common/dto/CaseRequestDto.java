package com.example.common.dto;

import java.util.List;
import com.example.common.enums.CaseStatus;
import com.example.common.enums.CaseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CaseRequestDto(CaseType type, @NotBlank(message = "Description cannot be blank") String description,
        @Valid CaseDetailsDto details,
        CaseStatus status, @NotEmpty(message = "At least one user must be assigned") List<Long> assignedUserIds) {

}
