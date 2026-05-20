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
import com.example.common.dto.UserRequestDto;
import com.example.common.dto.UserResponseDto;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Operations related to user account management and administration")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Gets user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @Operation(summary = "Gets all users", description = "With optional pagination")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Creates a new user")
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto dto,
            UriComponentsBuilder ucb) {
        UserResponseDto createdUser = userService.createUser(dto);
        URI location = ucb.path("/api/users/{id}").buildAndExpand(createdUser.id()).toUri();
        return ResponseEntity.created(location).body(createdUser);
    }

    @Operation(summary = "Fully updates user by ID", description = "User must exist")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto dto) {
        userService.updateUser(id, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletes user by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
