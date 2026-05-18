package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import com.example.common.dto.UserNotificationDto;

@Component
public class UserNotificationPublisher {
    @Autowired
    private JmsTemplate jmsTemplate;

    public void publishUserNotification(String title, String message, Long userId) {
        jmsTemplate.convertAndSend("notification.queue",
                new UserNotificationDto(title, message, false, userId));
    }
}
