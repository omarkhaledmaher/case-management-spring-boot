package com.example.web.controller;

import java.net.URI;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.common.dto.RoleRequestDto;
import com.example.common.dto.RoleResponseDto;
import com.example.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Operations related to role and privilege management")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Operation(summary = "Gets role by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable Long id) {
        RoleResponseDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @Operation(summary = "Gets all roles",
            description = "With optional pagination")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleResponseDto>> getAllRoles(@ParameterObject Pageable pageable) {
        List<RoleResponseDto> roles = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(roles);
    }

    @Operation(
            summary = "Creates a new role",
            description = " If privileges do not exist, they will be created.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody RoleRequestDto dto,
            UriComponentsBuilder ucb) {
        RoleResponseDto createdRole = roleService.createRole(dto);
        URI location = ucb.path("/api/roles/{id}").buildAndExpand(createdRole.id()).toUri();
        return ResponseEntity.created(location).body(createdRole);
    }

    @Operation(summary = "Fully updates role",
            description = "If privileges do not exist, they will be created.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponseDto> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequestDto dto) {
        roleService.updateRole(id, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Deletes role by ID",
            description = "Does not delete any privileges")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponseDto> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
