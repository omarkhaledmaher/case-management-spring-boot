package com.example.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import com.example.common.dto.UserNotificationDto;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.UserNotificationMapper;
import com.example.model.User;
import com.example.repository.UserNotificationRepository;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserNotificationService {
    private final UserNotificationMapper mapper;
    private final UserNotificationRepository repository;
    private final UserRepository userRepository;

    public List<UserNotificationDto> getAllUserNotifications(Long userId, String username, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        if (!user.getUsername().equals(username)) {
            throw new AuthorizationDeniedException("Unauthorized access to notifications");
        }

        return repository.findByUserId(userId, pageable).stream().map(mapper::toDto).toList();
    }
}
