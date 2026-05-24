package com.example.service;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import com.example.common.dto.EventDto;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EventListener {
    private final JmsTemplate jmsTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEvent(EventDto dto) {
        jmsTemplate.convertAndSend("database.logging", dto);
    }

}
