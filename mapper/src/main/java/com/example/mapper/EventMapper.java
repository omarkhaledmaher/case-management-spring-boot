package com.example.mapper;

import org.springframework.stereotype.Component;
import com.example.common.dto.EventDto;
import com.example.model.Event;
import com.example.model.EventCode;

@Component
public class EventMapper {
    public Event toEvent(EventDto dto) {
        Event event = new Event();
        event.setCode(new EventCode(dto.entityName(), dto.operation()));

        StringBuilder sb = new StringBuilder();
        sb.append("Event logged: ").append(dto.operation()).append(" - ")
                .append(dto.entityName()).append(" ").append(dto.methodName()).append(" by user: ")
                .append(dto.username()).append(" with response: ").append(dto.response());

        event.setDescription(sb.toString());
        return event;
    }
}
