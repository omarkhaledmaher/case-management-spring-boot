package com.example.service.mapper;

import org.springframework.stereotype.Component;
import com.example.common.events.DatabaseLoggingEvent;
import com.example.model.Event;
import com.example.model.EventCode;

@Component
public class EventMapper {
    public Event toEvent(DatabaseLoggingEvent databaseLoggingEvent) {
        Event event = new Event();
        event.setCode(new EventCode(databaseLoggingEvent.entityName(), databaseLoggingEvent.operation()));

        StringBuilder sb = new StringBuilder();
        sb.append("Event logged: ").append(databaseLoggingEvent.operation()).append(" - ")
                .append(databaseLoggingEvent.entityName()).append(" ").append(databaseLoggingEvent.methodName())
                .append(" with response: ").append(databaseLoggingEvent.response());

        event.setDescription(sb.toString());
        return event;
    }
}
