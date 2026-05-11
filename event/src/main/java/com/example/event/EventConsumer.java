package com.example.event;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.events.DatabaseLoggingEvent;
import com.example.repository.EventRepository;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EventConsumer {
    private final EventRepository repository;
    private final EventMapper mapper;

    @Transactional
    @JmsListener(destination = "database.logging")
    public void createEvent(DatabaseLoggingEvent databaseLoggingEvent) {
        repository.save(mapper.toEvent(databaseLoggingEvent));
    }

}
