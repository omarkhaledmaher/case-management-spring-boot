package com.example.mapper;

import org.springframework.stereotype.Component;
import com.example.common.dto.UserNotificationDto;
import com.example.common.dto.UserNotificationResponseDto;
import com.example.model.UserNotification;

@Component
public class UserNotificationMapper {
    public UserNotification toUserNotification(UserNotificationDto dto) {
        UserNotification notification = new UserNotification();
        notification.setTitle(dto.title());
        notification.setMessage(dto.message());
        notification.setIsRead(dto.isRead());
        notification.setRecipient(dto.recipient());
        return notification;
    }

    public UserNotificationDto toDto(UserNotification notification) {
        return new UserNotificationDto(
                notification.getTitle(),
                notification.getMessage(),
                notification.getIsRead(),
                notification.getRecipient());
    }

    public UserNotificationResponseDto toResponseDto(Long id, UserNotificationDto dto) {
        return new UserNotificationResponseDto(id, dto.title(), dto.message(), dto.isRead());
    }

    public UserNotificationResponseDto toResponseDto(UserNotification notification) {
        return new UserNotificationResponseDto(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getIsRead());
    }
}
