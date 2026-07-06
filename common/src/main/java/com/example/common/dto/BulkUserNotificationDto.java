package com.example.common.dto;

import java.util.List;

public record BulkUserNotificationDto(String title, String message, List<String> recipients) {

}
