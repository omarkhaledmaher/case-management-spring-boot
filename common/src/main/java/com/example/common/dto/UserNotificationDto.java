package com.example.common.dto;

import java.util.List;

public record UserNotificationDto(String title, String message, Boolean isRead, List<Long> userIds) {

}
