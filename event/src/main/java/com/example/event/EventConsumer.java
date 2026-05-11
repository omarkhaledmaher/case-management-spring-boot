package com.example.event;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.EventDto;
import com.example.mapper.EventMapper;
import com.example.repository.EventRepository;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EventConsumer {
    private final EventRepository repository;
    private final EventMapper mapper;

    @Transactional
    @JmsListener(destination = "database.logging")
    public void createEvent(EventDto dto) {
        repository.save(mapper.toEvent(dto));
    }

}
