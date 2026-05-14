package com.example.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.example.common.dto.EventDto;
import com.example.common.enums.DatabaseOperation;

@Aspect
@Component
public class EventLoggingAspect {
    @Autowired
    private JmsTemplate jmsTemplate;

    @AfterReturning(pointcut = "within(@org.springframework.stereotype.Service *)", returning = "result")
    public void logEvent(JoinPoint joinPoint, Object result) {
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username =
                (authentication != null && authentication.isAuthenticated()) ? authentication.getName()
                        : "anonymousUser";

        String entityName = joinPoint.getTarget().getClass().getSimpleName().replace("Service", "");

        jmsTemplate.convertAndSend("database.logging",
                new EventDto(operation, entityName, methodName, username, result.toString()));
    }
}
