package com.example.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import com.example.common.enums.DatabaseOperation;
import com.example.common.events.DatabaseLoggingEvent;

@Aspect
@Component
public class EventLoggingAspect {
    @Autowired
    private JmsTemplate jmsTemplate;

    @AfterReturning(value = "execution(* com.example.service.*.*(..)) ", returning = "response")
    public void logEvent(JoinPoint joinPoint, Object response) {
        String methodName = joinPoint.getSignature().getName();

        DatabaseOperation operation;
        if (methodName.startsWith("create")) {
            operation = DatabaseOperation.CREATED;
        } else if (methodName.startsWith("update")) {
            operation = DatabaseOperation.UPDATED;
        } else if (methodName.startsWith("delete")) {
            operation = DatabaseOperation.DELETED;
        } else {
            return;
        }

        String entityName = joinPoint.getTarget().getClass().getSimpleName().replace("Service", "");

        jmsTemplate.convertAndSend("database.logging",
                new DatabaseLoggingEvent(operation, entityName, methodName, response.toString()));
    }
}
