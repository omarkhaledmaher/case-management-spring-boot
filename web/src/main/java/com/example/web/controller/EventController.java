package com.example.web.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.common.dto.EventResponseDto;
import com.example.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Retrieval of system events")
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Gets event by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event found and returned"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403", description = "Missing ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Event with specified ID not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @Operation(summary = "Gets all events", description = "With optional pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403", description = "Missing ADMIN role")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EventResponseDto>> getAllEvents(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(eventService.getAllEvents(pageable));
    }

}
