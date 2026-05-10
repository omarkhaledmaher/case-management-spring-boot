package com.example.common.events;

import com.example.common.enums.EventType;

public record DatabaseLoggingEvent(EventType eventType, String entityName, String methodName, String response) {

}
