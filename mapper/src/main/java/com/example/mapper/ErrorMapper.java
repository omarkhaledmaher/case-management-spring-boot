package com.example.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import com.example.common.dto.ErrorDto;
import com.example.common.dto.ErrorResponseDto;

@Component
public class ErrorMapper {

    public ErrorResponseDto toDto(Exception ex, Integer status, String path) {
        String type = ex.getClass().getSimpleName();
        String message = ex.getMessage();
        List<ErrorDto> errors = List.of();
        return new ErrorResponseDto(type, message, status, path, errors);
    }

    public ErrorResponseDto toDto(Exception ex, String message, Integer status, String path) {
        String type = ex.getClass().getSimpleName();
        List<ErrorDto> errors = List.of();
        return new ErrorResponseDto(type, message, status, path, errors);
    }

    public ErrorResponseDto toDto(Exception ex, String message, Integer status, String path, List<ErrorDto> errors) {
        String type = ex.getClass().getSimpleName();
        return new ErrorResponseDto(type, message, status, path, errors);
    }
}
