package com.example.common.dto;

import com.example.common.enums.DatabaseOperation;

public record EventDto(DatabaseOperation operation, String entityName, String methodName, String username,
        String response) {

}
