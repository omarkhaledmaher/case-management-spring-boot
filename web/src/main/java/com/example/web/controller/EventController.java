package com.example.web.controller;

import java.util.List;
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
import io.swagger.v3.oas.annotations.tags.Tag;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Retrieval of system events")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getEvent(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(Pageable pageable) {
        List<Event> events = eventService.getAllEvents(pageable);
        return ResponseEntity.ok(events);
    }

}
