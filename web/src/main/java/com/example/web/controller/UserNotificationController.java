package com.example.web.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.common.dto.UserNotificationResponseDto;
import com.example.service.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "User Notifications", description = "Operations related to user notifications and real-time updates")
public class UserNotificationController {
    private final UserNotificationService notificationService;

    @Operation(summary = "Gets user's notifications")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403", description = "Missing USER or ADMIN role")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<UserNotificationResponseDto>> getUserNotifications(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(notificationService.getAllUserNotifications(pageable));
    }

    @Operation(summary = "Gets number of unread notifications")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403", description = "Missing USER or ADMIN role")
    })
    @GetMapping("/count")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Long> getUnreadUserNotificationsCount() {
        return ResponseEntity.ok(notificationService.getUnreadUserNotificationCount());
    }

    @Operation(summary = "Marks user's unread notifications as read")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Missing USER or ADMIN role")
    })
    @PutMapping("/mark-all-read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> markUserNotificationsAsRead() {
        this.notificationService.markAllUserNotificationsAsRead();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletes notification by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notification deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403",
                    description = "Missing USER or ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Notification with specified ID not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        this.notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
