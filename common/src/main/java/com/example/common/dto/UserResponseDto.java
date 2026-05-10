package com.example.common.dto;

import java.util.List;

public record UserResponseDto(Long id, String username, List<String> roleNames) {

}
