package com.example.event;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private ApplicationEventPublisher eventPublisher;

    @AfterReturning(value = "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)", returning = "response")
    public void logEvent(JoinPoint joinPoint, Object response) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        java.lang.reflect.Method method = signature.getMethod();

        DatabaseOperation eventType;
        if (method.isAnnotationPresent(PostMapping.class)) {
            eventType = DatabaseOperation.CREATED;
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            eventType = DatabaseOperation.UPDATED;
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            eventType = DatabaseOperation.DELETED;
        } else {
            eventType = DatabaseOperation.UNKNOWN;
        }

        String entityName = joinPoint.getTarget().getClass().getSimpleName().replace("Controller", "");
        String methodName = joinPoint.getSignature().getName();

        eventPublisher.publishEvent(new DatabaseLoggingEvent(eventType, entityName, methodName, response.toString()));
    }
}
