package com.example.service;

import java.util.List;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import com.example.common.dto.BulkUserNotificationDto;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserNotificationPublisher {
    private final JmsTemplate jmsTemplate;

    public void publishUserNotification(String title, String message, List<String> recipients) {
        jmsTemplate.convertAndSend("notification.queue",
                new BulkUserNotificationDto(title, message, recipients));
    }
}
