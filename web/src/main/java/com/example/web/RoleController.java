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
import com.example.service.RoleService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/roles")
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
            UriComponentsBuilder ucb) {
        RoleResponseDto createdRole = roleService.createRole(dto);
        URI location = ucb.path("/api/admin/roles/{id}").buildAndExpand(createdRole.id()).toUri();
        return ResponseEntity.created(location).body(createdRole);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDto> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequestDto dto) {
        roleService.updateRole(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RoleResponseDto> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
