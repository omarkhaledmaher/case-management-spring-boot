package com.example.web.controller;

import java.net.URI;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.common.dto.CaseRequestDto;
import com.example.common.dto.CaseResponseDto;
import com.example.common.dto.ChatParticipantResponseDto;
import com.example.common.enums.CaseStatus;
import com.example.common.enums.CaseType;
import com.example.service.CaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
@Tag(name = "Cases", description = "Operations related to case management")
public class CaseController {
    private final CaseService caseService;

    @Operation(summary = "Gets case by ID", description = "User must have access to the case")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Case found and returned"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403", description = "Missing CASE_READ authority or no access to case"),
            @ApiResponse(responseCode = "404", description = "Case with specified ID not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and hasAuthority('CASE_READ'))")
    public ResponseEntity<CaseResponseDto> getCase(@PathVariable Long id) {
        CaseResponseDto caseResponse = caseService.getCaseById(id);
        return ResponseEntity.ok(caseResponse);
    }

    @Operation(summary = "Gets all cases",
            description = "Returns all cases user has access to, with optional search term for filtering")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cases retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403", description = "Missing CASE_READ authority")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and hasAuthority('CASE_READ'))")
    public ResponseEntity<Page<CaseResponseDto>> getAllCases(@RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) CaseType type, @RequestParam(required = false) CaseStatus status,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(caseService.getCases(searchTerm, type, status, pageable));
    }


    @Operation(summary = "Gets all users assigned to a case")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403", description = "Missing CASE_READ authority"),
            @ApiResponse(responseCode = "404", description = "Case does not exist or user is not assigned to it")
    })
    @GetMapping("/{id}/users")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and hasAuthority('CASE_READ'))")
    public ResponseEntity<Page<ChatParticipantResponseDto>> getAssignedUsers(@PathVariable Long id,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(caseService.getUsersByCaseId(id, pageable));
    }

    @Operation(summary = "Creates a new case", description = "Requires CASE_CREATE authority")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Case successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403", description = "Missing CASE_CREATE authority"),
            @ApiResponse(responseCode = "409", description = "Conflicting case data")
    })
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and hasAuthority('CASE_CREATE'))")
    @PostMapping
    public ResponseEntity<CaseResponseDto> createCase(@Valid @RequestBody CaseRequestDto dto,
            UriComponentsBuilder ucb) {
        CaseResponseDto createdCase = caseService.createCase(dto);
        URI location = ucb.path("/api/cases/{id}").buildAndExpand(createdCase.id()).toUri();
        return ResponseEntity.created(location).body(createdCase);
    }
}
