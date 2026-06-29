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
        event.setMethodName(dto.methodName());
        event.setUsername(dto.username());
        event.setResponse(dto.response());
        return event;
    }
}
