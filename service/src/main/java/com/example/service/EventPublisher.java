package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import com.example.common.dto.EventDto;
import com.example.common.enums.DatabaseOperation;

@Component
public class EventPublisher {
    @Autowired
    private JmsTemplate jmsTemplate;

    public void publishEvent(DatabaseOperation operation, String entityName, String methodName, String username,
            Object result) {
        jmsTemplate.convertAndSend("database.logging",
                new EventDto(operation, entityName, methodName, username, result.toString()));
    }
}
