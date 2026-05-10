package com.example.service;

import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.events.DatabaseLoggingEvent;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.model.Event;
import com.example.repository.EventRepository;
import com.example.service.mapper.EventMapper;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper mapper;

    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));
    }

    public List<Event> getAllEvents(Pageable pageable) {
        return eventRepository
                .findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort())).toList();
    }

    @Transactional
    @EventListener
    public Event createEvent(DatabaseLoggingEvent databaseLoggingEvent) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Event createdEvent = eventRepository.save(mapper.toEvent(databaseLoggingEvent, username));
        eventRepository.flush();
        return createdEvent;
    }

}
