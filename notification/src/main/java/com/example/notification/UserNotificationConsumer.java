package com.example.notification;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.UserNotificationDto;
import com.example.mapper.UserNotificationMapper;
import com.example.repository.UserNotificationRepository;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserNotificationConsumer {
    private final UserNotificationRepository repository;
    private final UserNotificationMapper mapper;

    @Transactional
    @JmsListener(destination = "notification.queue")
    public void createNotification(UserNotificationDto dto) {
        repository.save(mapper.toUserNotification(dto));
    }
}
