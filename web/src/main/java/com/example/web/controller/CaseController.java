package com.example.web.controller;

import java.net.URI;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.service.CaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cases")
@Tag(name = "Cases", description = "Operations related to case management")
public class CaseController {
    @Autowired
    private CaseService caseService;

    @Operation(summary = "Gets case by ID", description = "User must have access to the case")
    @GetMapping("/{id}")
    public ResponseEntity<CaseResponseDto> getCase(@PathVariable Long id) {
        CaseResponseDto caseResponse = caseService.getCaseById(id);
        return ResponseEntity.ok(caseResponse);
    }

    @Operation(summary = "Gets all cases",
            description = "Returns all cases user has access to, with optional search term for filtering")
    @GetMapping
    public ResponseEntity<List<CaseResponseDto>> getAllCases(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(caseService.getAllCases(pageable));
    }

    @Operation(summary = "Searches cases", description = "Searches based on case details")
    @GetMapping("/search")
    public ResponseEntity<List<CaseResponseDto>> searchCases(@RequestParam(required = false) String searchTerm,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(caseService.searchCases(searchTerm, pageable));
    }

    @Operation(summary = "Creates a new case", description = "Requires CASE_CREATE authority")
    @PreAuthorize("hasAuthority('CASE_CREATE')")
    @PostMapping
    public ResponseEntity<CaseResponseDto> createCase(@Valid @RequestBody CaseRequestDto dto,
            UriComponentsBuilder ucb) {
        CaseResponseDto createdCase = caseService.createCase(dto);
        URI location = ucb.path("/api/cases/{id}").buildAndExpand(createdCase.id()).toUri();
        return ResponseEntity.created(location).body(createdCase);
    }
}
