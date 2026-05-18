package com.example.service;

import java.util.List;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.ChatMessageRequestDto;
import com.example.common.dto.ChatMessageResponseDto;
import com.example.common.enums.DatabaseOperation;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.ChatMessageMapper;
import com.example.model.Case;
import com.example.model.Chat;
import com.example.model.ChatMessage;
import com.example.model.User;
import com.example.repository.ChatMessageRepository;
import com.example.repository.ChatRepository;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChatMessageService {
    private final ChatMessageMapper mapper;
    private final ChatMessageRepository repository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;
    private final UserNotificationPublisher userNotificationPublisher;

    @Transactional
    public ChatMessageResponseDto createChatMessage(Long chatId, ChatMessageRequestDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

        Chat chat = chatRepository.findByIdAndParticipants(chatId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with id " + chatId + " not found"));

        ChatMessage createdMessage = repository.save(mapper.toChatMessage(dto, user, chat));
        repository.flush();
        ChatMessageResponseDto responseDto = mapper.toDto(createdMessage);
        eventPublisher.publishEvent(DatabaseOperation.CREATED, "ChatMessage", "createChatMessage", username,
                responseDto);

        publishChatNotification(chat, username);
        return responseDto;
    }

    private void publishChatNotification(Chat chat, String username) {
        List<User> participants = chat.getParticipants();
        Case chatCase = chat.getChatCase();
        participants.stream()
                .filter(p -> !p.getUsername().equals(username))
                .forEach(p -> userNotificationPublisher.publishUserNotification(
                        "New message in chat",
                        String.format("You have a new message from %s in case %s", username, chatCase.getDescription()),
                        p.getId()));
    }
}
