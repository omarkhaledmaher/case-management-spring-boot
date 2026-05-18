package com.example.web;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.common.dto.UserNotificationDto;
import com.example.service.UserNotificationService;

@RestController
@RequestMapping("/api/notifications")
public class UserNotificationController {
    @Autowired
    private UserNotificationService notificationService;

    @GetMapping
    public List<UserNotificationDto> getUserNotifications(Pageable pageable) {
        return notificationService.getAllUserNotifications(pageable);
    }

}
