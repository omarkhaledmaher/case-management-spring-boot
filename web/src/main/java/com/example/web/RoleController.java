package com.example.web;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
import com.example.security.CurrentUser;
import com.example.service.RoleService;
import jakarta.validation.Valid;

@RestController
@RequestMapping({"/api/admin/roles", "/api/test/roles"})
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable Long id) {
        RoleResponseDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> getAllRoles(Pageable pageable) {
        List<RoleResponseDto> roles = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody RoleRequestDto dto,
            @CurrentUser String username, UriComponentsBuilder ucb) {
        RoleResponseDto createdRole = roleService.createRole(dto, username);
        URI location = ucb.path("/api/admin/roles/{id}").buildAndExpand(createdRole.id()).toUri();
        return ResponseEntity.created(location).body(createdRole);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDto> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequestDto dto,
            @CurrentUser String username) {
        roleService.updateRole(id, dto, username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RoleResponseDto> deleteRole(@PathVariable Long id, @CurrentUser String username) {
        roleService.deleteRole(id, username);
        return ResponseEntity.noContent().build();
    }
}
