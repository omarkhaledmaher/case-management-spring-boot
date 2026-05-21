package com.example.web;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
public class GlobalMessageExceptionHandler {

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public String handleException(Exception ex) {
        log.info("WebSocket error: {}", ex.getMessage());
        return ex.getMessage();
    }
}

