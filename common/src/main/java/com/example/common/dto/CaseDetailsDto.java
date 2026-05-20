package com.example.common.dto;

import jakarta.validation.constraints.NotBlank;

public record CaseDetailsDto(@NotBlank(message = "Customer name cannot be blank") String customerName,
        @NotBlank(message = "Applicant name cannot be blank") String applicantName,
        @NotBlank(message = "Reference name cannot be blank") String referenceName) {

}
