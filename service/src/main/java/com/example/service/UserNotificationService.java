package com.example.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.UserNotificationResponseDto;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.UserNotificationMapper;
import com.example.model.User;
import com.example.model.UserNotification;
import com.example.repository.UserNotificationRepository;
import com.example.repository.UserRepository;
import com.example.security.IAuthFacade;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserNotificationService {
    private final UserNotificationMapper mapper;
    private final UserNotificationRepository repository;
    private final UserRepository userRepository;
    private final IAuthFacade authFacade;

    @Transactional
    public List<UserNotificationResponseDto> getAllUserNotifications(Pageable pageable) {
        String username = authFacade.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found"));

        List<UserNotification> notifications = repository.findByUserId(user.getId(), pageable);
        List<UserNotificationResponseDto> notificationDtos = notifications.stream().map(mapper::toResponseDto).toList();
        notifications.forEach(notification -> notification.setIsRead(true));
        repository.saveAll(notifications);
        return notificationDtos;
    }
}
