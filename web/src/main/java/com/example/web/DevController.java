package com.example.web;

import java.net.URI;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.common.dto.RoleRequestDto;
import com.example.common.dto.RoleResponseDto;
import com.example.common.dto.UserRequestDto;
import com.example.common.dto.UserResponseDto;
import com.example.service.RoleService;
import com.example.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@Profile("dev")
@RequestMapping("/api/dev")
@AllArgsConstructor
public class DevController {
    private final UserService userService;
    private final RoleService roleService;

    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto dto,
            UriComponentsBuilder ucb) {
        UserResponseDto createdUser = userService.createUser(dto);
        URI location = ucb.path("/users/{id}").buildAndExpand(createdUser.id()).toUri();
        return ResponseEntity.created(location).body(createdUser);
    }

    @PostMapping("/roles")
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody RoleRequestDto dto,
            UriComponentsBuilder ucb) {
        RoleResponseDto createdRole = roleService.createRole(dto);
        URI location = ucb.path("/api/roles/{id}").buildAndExpand(createdRole.id()).toUri();
        return ResponseEntity.created(location).body(createdRole);
    }
}
