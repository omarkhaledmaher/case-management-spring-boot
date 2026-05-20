package com.example.web.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.common.dto.UserNotificationResponseDto;
import com.example.service.UserNotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;

@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "User Notifications", description = "Operations related to user notifications and real-time updates")
public class UserNotificationController {
    @Autowired
    private UserNotificationService notificationService;

    @GetMapping("/subscribe")
    public SseEmitter subscribeToNotifications() {
        return notificationService.subscribe();
    }

    @GetMapping
    public List<UserNotificationResponseDto> getUserNotifications(Pageable pageable) {
        return notificationService.getAllUserNotifications(pageable);
    }

}
