package com.example.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.example.common.dto.EventDto;
import com.example.common.enums.DatabaseOperation;
import com.example.security.IAuthFacade;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EventPublisher {
    private final IAuthFacade authFacade;
    private final ApplicationEventPublisher applicationEventPublisher;
    public void publishEvent(DatabaseOperation operation, String entityName, String methodName, Object result) {
        String username = authFacade.getUsername();
        applicationEventPublisher
                .publishEvent(new EventDto(operation, entityName, methodName, username, result.toString()));
    }

    public void publishEvent(DatabaseOperation operation, String entityName, String methodName, String username,
            Object result) {
        applicationEventPublisher
                .publishEvent(new EventDto(operation, entityName, methodName, username, result.toString()));
    }
}
