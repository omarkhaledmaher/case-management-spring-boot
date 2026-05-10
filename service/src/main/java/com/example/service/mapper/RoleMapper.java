package com.example.service.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import com.example.common.dto.RoleRequestDto;
import com.example.common.dto.RoleResponseDto;
import com.example.model.Privilege;
import com.example.model.Role;

@Component
public class RoleMapper {
    public Role toRole(RoleRequestDto dto, List<Privilege> privileges) {
        Role role = new Role();
        role.setName(dto.name());
        role.getPrivileges().addAll(privileges);
        return role;
    }

    public RoleResponseDto toDto(Role role) {
        List<String> privileges = role.getPrivileges().stream().map(Privilege::getName).toList();
        return new RoleResponseDto(role.getId(), role.getName(), privileges);
    }

}
