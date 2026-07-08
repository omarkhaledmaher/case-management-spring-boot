package com.example.common.dto;

import java.util.List;

public record ErrorResponseDto(String type, String message, Integer status, String path, List<ErrorDto> errors) {

}
