package com.example.event;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import com.example.common.enums.EventType;
import com.example.model.Event;
import com.example.model.EventCode;
import com.example.service.EventService;

@Aspect
@Component
public class EventLoggingAspect {
    @Autowired
    private EventService eventService;

    @AfterReturning(value = "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)", returning = "response")
    public void logEvent(JoinPoint joinPoint, Object response) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        java.lang.reflect.Method method = signature.getMethod();

        EventType type;
        if (method.isAnnotationPresent(PostMapping.class)) {
            type = EventType.CREATED;
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            type = EventType.UPDATED;
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            type = EventType.DELETED;
        } else {
            type = EventType.UNKNOWN;
        }

        String className = joinPoint.getTarget().getClass().getSimpleName().replace("Controller", "");
        String methodName = joinPoint.getSignature().getName();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Event event = new Event();
        event.setCode(new EventCode(className, type));

        StringBuilder sb = new StringBuilder();
        sb.append("Event logged: ").append(type).append(" - ").append(className).append(" ").append(methodName)
                .append(" by user: ").append(username).append(" with response: ").append(response);

        event.setDescription(sb.toString());
        eventService.createEvent(event);
    }
}
