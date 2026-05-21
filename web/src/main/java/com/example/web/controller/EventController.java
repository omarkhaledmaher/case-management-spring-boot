package com.example.web.controller;

import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.model.Event;
import com.example.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Retrieval of system events")
public class EventController {
    @Autowired
    private EventService eventService;

    @Operation(summary = "Gets event by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getEvent(id);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "Gets all events", description = "With optional pagination")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Event>> getAllEvents(@ParameterObject Pageable pageable) {
        List<Event> events = eventService.getAllEvents(pageable);
        return ResponseEntity.ok(events);
    }

}
