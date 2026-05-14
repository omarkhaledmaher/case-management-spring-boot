package com.example.web;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.common.dto.UserNotificationDto;
import com.example.security.CurrentUser;
import com.example.service.UserNotificationService;

@RestController
@RequestMapping("/api/notifications")
public class UserNotificationController {
    @Autowired
    private UserNotificationService notificationService;

    @GetMapping("/user/{userId}")
    public List<UserNotificationDto> getUserNotifications(@PathVariable Long userId, Pageable pageable,
            @CurrentUser String username) {
        return notificationService.getAllUserNotifications(userId, username, pageable);
    }

}
