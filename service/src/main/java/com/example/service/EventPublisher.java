package com.example.service;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import com.example.common.dto.EventDto;
import com.example.common.enums.DatabaseOperation;
import com.example.security.IAuthFacade;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EventPublisher {
    private final IAuthFacade authFacade;
    private final JmsTemplate jmsTemplate;

    public void publishEvent(DatabaseOperation operation, String entityName, String methodName, Object result) {
        String username = authFacade.getAuthentication().getName();
        jmsTemplate.convertAndSend("database.logging",
                new EventDto(operation, entityName, methodName, username, result.toString()));
    }

    public void publishEvent(DatabaseOperation operation, String entityName, String methodName, String username,
            Object result) {
        jmsTemplate.convertAndSend("database.logging",
                new EventDto(operation, entityName, methodName, username, result.toString()));
    }
}
