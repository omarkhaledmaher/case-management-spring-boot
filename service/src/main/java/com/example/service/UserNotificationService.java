package com.example.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.common.dto.UserNotificationResponseDto;
import com.example.common.enums.DatabaseOperation;
import com.example.mapper.UserNotificationMapper;
import com.example.model.User;
import com.example.model.UserNotification;
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
    private final EventPublisher eventPublisher;

    public Page<UserNotificationResponseDto> getAllUserNotifications(Pageable pageable) {
        String username = authFacade.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
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

    public void deleteNotification(Long id) {
        Optional<UserNotification> notification = repository.findById(id);

        if (notification.isEmpty()) {
            return;
        }

        String username = authFacade.getUsername();
        if (!notification.get().getRecipient().equals(username)) {
            throw new AuthorizationDeniedException("No access to notification with id " + id);
        }

        UserNotificationResponseDto dto = mapper.toResponseDto(notification.get());
        eventPublisher.publishEvent(DatabaseOperation.DELETED, "UserNotification", "deleteUserNotification", dto);

        repository.deleteById(id);
    }
}
