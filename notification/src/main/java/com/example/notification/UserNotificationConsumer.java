package com.example.notification;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.UserNotificationDto;
import com.example.common.dto.UserNotificationResponseDto;
import com.example.mapper.UserNotificationMapper;
import com.example.repository.UserNotificationRepository;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserNotificationConsumer {
    private final UserNotificationRepository repository;
    private final UserNotificationMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    @JmsListener(destination = "notification.queue")
    public void createNotification(UserNotificationDto dto) {
        Long id = repository.save(mapper.toUserNotification(dto)).getId();
        UserNotificationResponseDto payload = mapper.toResponseDto(id, dto);
        messagingTemplate.convertAndSendToUser(dto.recipient(), "/queue/notifications", payload);
    }
}
