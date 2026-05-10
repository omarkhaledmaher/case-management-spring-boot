package com.example.service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.model.Event;
import com.example.repository.EventRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));
    }

    public List<Event> getAllEvents(Pageable pageable) {
        return eventRepository
                .findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort())).toList();
    }

    @Transactional
    public Event createEvent(Event event) {
        Event createdEvent = eventRepository.save(event);
        eventRepository.flush();
        return createdEvent;
    }

}
