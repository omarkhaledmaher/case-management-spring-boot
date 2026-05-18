package com.example.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.common.dto.UserNotificationDto;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.UserNotificationMapper;
import com.example.model.User;
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

    public List<UserNotificationDto> getAllUserNotifications(Pageable pageable) {
        String username = authFacade.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found"));

        return repository.findByUserId(user.getId(), pageable).stream().map(mapper::toDto).toList();
    }
}
