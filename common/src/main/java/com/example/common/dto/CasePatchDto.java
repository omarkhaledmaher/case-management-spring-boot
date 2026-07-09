package com.example.common.dto;

import com.example.common.enums.CaseStatus;
import com.example.common.enums.CaseType;
import jakarta.annotation.Nullable;

public record CasePatchDto(@Nullable String name, @Nullable CaseType type, @Nullable String description,
        @Nullable CaseDetailsDto details, @Nullable CaseStatus status) {

}
