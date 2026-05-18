package com.example.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.domain.Pageable;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.common.dto.UserNotificationDto;
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
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

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

    public SseEmitter subscribe() {
        String username = authFacade.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found"));

        SseEmitter emitter = new SseEmitter(-1L);

        emitters.put(user.getId(), emitter);

        emitter.onCompletion(() -> emitters.remove(user.getId()));
        emitter.onTimeout(() -> emitters.remove(user.getId()));
        emitter.onError((e) -> emitters.remove(user.getId()));

        return emitter;
    }

    @JmsListener(destination = "notification.queue")
    public void sendNotification(UserNotificationDto dto) {
        SseEmitter emitter = emitters.get(dto.userId());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(dto));
            } catch (IOException e) {
                emitters.remove(dto.userId());
            }
        }
    }
}
