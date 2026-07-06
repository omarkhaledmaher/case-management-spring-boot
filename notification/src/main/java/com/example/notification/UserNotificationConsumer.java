package com.example.notification;

import java.util.List;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.BulkUserNotificationDto;
import com.example.common.dto.UserNotificationResponseDto;
import com.example.mapper.UserNotificationMapper;
import com.example.model.UserNotification;
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
    public void createNotification(BulkUserNotificationDto bulkDto) {
        List<UserNotification> notifications = mapper.toUserNotifications(bulkDto);
        List<UserNotification> newNotifications = repository.saveAll(notifications);
        for (var notification : newNotifications) {
            UserNotificationResponseDto payload = mapper.toResponseDto(notification);
            messagingTemplate.convertAndSendToUser(notification.getRecipient(), "/queue/notifications", payload);
        }
    }
}
