package com.example.web.controller;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@Profile("dev")
@RequestMapping("/api/dev")
@AllArgsConstructor
@Tag(name = "Development", description = "Endpoints for development and testing purposes only")
@ApiResponse(responseCode = "400", description = "Invalid request body")
public class DevController {
    private final UserService userService;
    private final RoleService roleService;

    @Operation(summary = "Create a test user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "409", description = "Conflicting username")
    })
    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto dto,
            UriComponentsBuilder ucb) {
        UserResponseDto createdUser = userService.createUser(dto);
        URI location = ucb.path("/users/{id}").buildAndExpand(createdUser.id()).toUri();
        return ResponseEntity.created(location).body(createdUser);
    }

    @Operation(summary = "Create a test role", description = "If privileges do not exist, they will be created.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Role successfully created"),
            @ApiResponse(responseCode = "409", description = "Conflicting role name")
    })
    @PostMapping("/roles")
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody RoleRequestDto dto,
            UriComponentsBuilder ucb) {
        RoleResponseDto createdRole = roleService.createRole(dto);
        URI location = ucb.path("/api/roles/{id}").buildAndExpand(createdRole.id()).toUri();
        return ResponseEntity.created(location).body(createdRole);
    }
}
