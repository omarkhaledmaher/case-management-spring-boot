package com.example.common.dto;

import java.util.List;
import com.example.common.enums.CaseStatus;
import com.example.common.enums.CaseType;

public record CaseRequestDto(CaseType type, String description, CaseDetailsDto details, CaseStatus status,
        List<Long> assignedUserIds) {

}
