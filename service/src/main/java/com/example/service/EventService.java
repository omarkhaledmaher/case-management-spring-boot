package com.example.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.example.common.dto.EventResponseDto;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.EventMapper;
import com.example.model.Event;
import com.example.repository.EventRepository;
import com.example.repository.specification.EventSpecification;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper mapper;

    public EventResponseDto getEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));
        return mapper.toDto(event);
    }

    public Page<EventResponseDto> getAllEvents(String searchTerm, Pageable pageable) {
        Specification<Event> spec = Specification.unrestricted();
        spec = EventSpecification.hasSearchTerm(searchTerm);

        return eventRepository.findAll(spec, pageable).map(mapper::toDto);
    }
}
