package com.example.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.model.Event;
import com.example.repository.EventRepository;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));
    }

    public List<Event> getAllEvents(Pageable pageable) {
        return eventRepository
                .findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort())).toList();
    }
}
