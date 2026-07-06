package com.example.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.common.dto.UserNotificationResponseDto;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.UserNotificationMapper;
import com.example.model.User;
import com.example.repository.UserNotificationRepository;
import com.example.repository.UserRepository;
import com.example.security.IAuthFacade;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserNotificationService {
    private final UserNotificationMapper mapper;
    private final UserNotificationRepository repository;
    private final UserRepository userRepository;
    private final IAuthFacade authFacade;

    public Page<UserNotificationResponseDto> getAllUserNotifications(Pageable pageable) {
        String username = authFacade.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found"));
        return repository.findByRecipient(user.getUsername(), pageable).map(mapper::toResponseDto);
    }

    @Transactional
    public void markAllUserNotificationsAsRead() {
        String username = authFacade.getUsername();
        repository.markAllUserNotificationsAsRead(username);
    }

    public Long getUnreadUserNotificationCount() {
        String username = authFacade.getUsername();
        return repository.countByRecipientAndIsRead(username, false);
    }
}
