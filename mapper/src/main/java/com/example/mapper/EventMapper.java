package com.example.mapper;

import org.springframework.stereotype.Component;
import com.example.common.dto.EventCodeDto;
import com.example.common.dto.EventDto;
import com.example.common.dto.EventResponseDto;
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

    private EventCodeDto toDto(EventCode code) {
        return new EventCodeDto(code.getEntityName(), code.getOperation());
    }

    public EventResponseDto toDto(Event event) {
        return new EventResponseDto(event.getId(), toDto(event.getCode()), event.getTimestamp(), event.getMethodName(),
                event.getUsername(), event.getResponse());
    }
}
