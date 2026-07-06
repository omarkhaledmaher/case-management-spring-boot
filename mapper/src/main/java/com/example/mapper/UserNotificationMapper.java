package com.example.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.example.common.dto.BulkUserNotificationDto;
import com.example.common.dto.UserNotificationDto;
import com.example.common.dto.UserNotificationResponseDto;
import com.example.model.UserNotification;

@Component
public class UserNotificationMapper {
    public UserNotification toUserNotification(UserNotificationDto dto) {
        UserNotification notification = new UserNotification();
        notification.setTitle(dto.title());
        notification.setMessage(dto.message());
        notification.setRecipient(dto.recipient());
        return notification;
    }

    public List<UserNotification> toUserNotifications(BulkUserNotificationDto bulkDto) {
        List<UserNotification> notifications = new ArrayList<UserNotification>();
        for (String recipient : bulkDto.recipients()) {
            UserNotification notification = new UserNotification();
            notification.setTitle(bulkDto.title());
            notification.setMessage(bulkDto.message());
            notification.setIsRead(false);
            notification.setRecipient(recipient);
            notifications.add(notification);
        }
        return notifications;
    }

    public UserNotificationDto toDto(UserNotification notification) {
        return new UserNotificationDto(
                notification.getTitle(),
                notification.getMessage(),
                notification.getRecipient());
    }

    public UserNotificationResponseDto toResponseDto(Long id, UserNotificationDto dto) {
        return new UserNotificationResponseDto(id, dto.title(), dto.message(), false);
    }

    public UserNotificationResponseDto toResponseDto(UserNotification notification) {
        return new UserNotificationResponseDto(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getIsRead());
    }
}
