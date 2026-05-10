package com.example.service.mapper;

import org.springframework.stereotype.Component;
import com.example.common.events.DatabaseLoggingEvent;
import com.example.model.Event;
import com.example.model.EventCode;

@Component
public class EventMapper {
    public Event toEvent(DatabaseLoggingEvent databaseLoggingEvent, String username) {
        Event event = new Event();
        event.setCode(new EventCode(databaseLoggingEvent.entityName(), databaseLoggingEvent.eventType()));

        StringBuilder sb = new StringBuilder();
        sb.append("Event logged: ").append(databaseLoggingEvent.eventType()).append(" - ")
                .append(databaseLoggingEvent.entityName()).append(" ").append(databaseLoggingEvent.methodName())
                .append(" by user: ").append(username).append(" with response: ")
                .append(databaseLoggingEvent.response());

        event.setDescription(sb.toString());
        return event;
    }
}
