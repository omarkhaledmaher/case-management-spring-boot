package com.example.web.controller;

import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.common.dto.UserNotificationResponseDto;
import com.example.service.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "User Notifications", description = "Operations related to user notifications and real-time updates")
public class UserNotificationController {
    @Autowired
    private UserNotificationService notificationService;

    @Operation(summary = "Subscribes user to notifications",
            description = "Subscribes the user to real-time notifications using SSE")
    @GetMapping("/subscribe")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public SseEmitter subscribeToNotifications() {
        return notificationService.subscribe();
    }

    @Operation(
            summary = "Gets user's notifications",
            description = "Received notifications will be marked as read")
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserNotificationResponseDto>> getUserNotifications(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(notificationService.getAllUserNotifications(pageable));
    }

}
