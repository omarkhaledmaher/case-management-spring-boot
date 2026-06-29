package com.example.common.dto;

import com.example.common.enums.DatabaseOperation;

public record EventCodeDto(String entityName, DatabaseOperation operation) {

}
