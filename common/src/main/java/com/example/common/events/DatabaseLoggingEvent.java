package com.example.common.events;

import com.example.common.enums.DatabaseOperation;

public record DatabaseLoggingEvent(DatabaseOperation operation, String entityName, String methodName, String response) {

}
