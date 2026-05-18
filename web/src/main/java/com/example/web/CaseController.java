package com.example.web;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/cases")
public class CaseController {
    @Autowired
    private CaseService caseService;

    @GetMapping("/{id}")
    public ResponseEntity<CaseResponseDto> getCase(@PathVariable Long id) {
        CaseResponseDto caseResponse = caseService.getCaseById(id);
        return ResponseEntity.ok(caseResponse);
    }

    @GetMapping
    public ResponseEntity<List<CaseResponseDto>> getAllCases(Pageable pageable) {
        return ResponseEntity.ok(caseService.getAllCases(pageable));
    }


    @GetMapping("/search")
    public ResponseEntity<List<CaseResponseDto>> searchCases(@RequestParam(required = false) String searchTerm,
            Pageable pageable) {
        return ResponseEntity.ok(caseService.searchCases(searchTerm, pageable));
    }

    @PostMapping
    public ResponseEntity<CaseResponseDto> createCase(@RequestBody CaseRequestDto dto, UriComponentsBuilder ucb) {
        CaseResponseDto createdCase = caseService.createCase(dto);
        URI location = ucb.path("/api/cases/{id}").buildAndExpand(createdCase.id()).toUri();
        return ResponseEntity.created(location).body(createdCase);
    }
}
