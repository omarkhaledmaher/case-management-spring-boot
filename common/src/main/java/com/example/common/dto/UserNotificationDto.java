package com.example.common.dto;

public record UserNotificationDto(String title, String message, Boolean isRead, String recipient) {

}
