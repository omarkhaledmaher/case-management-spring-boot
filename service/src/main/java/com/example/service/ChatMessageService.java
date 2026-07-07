package com.example.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
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
import com.example.security.IAuthFacade;
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
    private final IAuthFacade authFacade;

    @Transactional
    public ChatMessageResponseDto createChatMessage(Long chatId, ChatMessageRequestDto dto) {
        String username = authFacade.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

        Chat chat = chatRepository.findByIdAndParticipants(chatId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with id " + chatId + " not found"));

        ChatMessage createdMessage = repository.save(mapper.toChatMessage(dto, user, chat));
        chat.setLastMessageAt(Instant.now());

        ChatMessageResponseDto responseDto = mapper.toDto(createdMessage);
        eventPublisher.publishEvent(DatabaseOperation.CREATED, "ChatMessage", "createChatMessage", username,
                responseDto);

        publishChatNotification(chat, dto.text(), username);
        return responseDto;
    }

    public List<ChatMessageResponseDto> getAllMessagesByChatId(Long chatId) {
        String username = authFacade.getUsername();
        Sort sort = Sort.by(Sort.Direction.ASC, "timestamp");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

        if (!chatRepository.existsByIdAndParticipants(chatId, user)) {
            throw new ResourceNotFoundException("Chat with id " + chatId + " not found");
        }

        List<ChatMessage> messages = repository.findByChatId(chatId, sort);

        return messages.stream()
                .sorted(Comparator.comparing(ChatMessage::getTimestamp))
                .map(mapper::toDto)
                .toList();
    }

    private void publishChatNotification(Chat chat, String message, String sender) {
        Set<User> allParticipants = chat.getParticipants();
        Case chatCase = chat.getChatCase();
        String shortMessage = message.substring(0, Math.min(message.length(), 125)).concat("...");

        List<String> participants =
                allParticipants.stream().map((u) -> u.getUsername()).filter(u -> !u.equals(sender)).toList();

        userNotificationPublisher.publishUserNotification(
                "New message from " + sender + " in case " + chatCase.getName(), shortMessage, participants);
    }
}
