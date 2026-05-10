package com.example.common.dto;

import java.util.List;

public record RoleResponseDto(Long id, String name, List<String> privileges) {

}
