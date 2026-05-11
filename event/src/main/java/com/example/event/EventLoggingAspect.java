package com.example.event;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import com.example.common.enums.DatabaseOperation;
import com.example.common.events.DatabaseLoggingEvent;

@Aspect
@Component
public class EventLoggingAspect {
    @Autowired
    private JmsTemplate jmsTemplate;

    @AfterReturning(
            value = "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
                    "@annotation(org.springframework.web.bind.annotation.DeleteMapping)) " +
                    "&& !within(com.example.web.AuthController)",
            returning = "response")
    public void logEvent(JoinPoint joinPoint, Object response) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        java.lang.reflect.Method method = signature.getMethod();

        DatabaseOperation operation;
        if (method.isAnnotationPresent(PostMapping.class)) {
            operation = DatabaseOperation.CREATED;
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            operation = DatabaseOperation.UPDATED;
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            operation = DatabaseOperation.DELETED;
        } else {
            operation = DatabaseOperation.UNKNOWN;
        }

        String entityName = joinPoint.getTarget().getClass().getSimpleName().replace("Controller", "");
        String methodName = joinPoint.getSignature().getName();

        jmsTemplate.convertAndSend("database.logging",
                new DatabaseLoggingEvent(operation, entityName, methodName, response.toString()));
    }
}
