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
        notification.setUserId(dto.userId());
        return notification;
    }

    public UserNotificationDto toDto(UserNotification notification) {
        return new UserNotificationDto(
                notification.getTitle(),
                notification.getMessage(),
                notification.getIsRead(),
                notification.getUserId());
    }

    public UserNotificationResponseDto toResponseDto(UserNotification notification) {
        return new UserNotificationResponseDto(
                notification.getTitle(),
                notification.getMessage(),
                notification.getIsRead());
    }
}
