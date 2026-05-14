package com.example.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.ChatMessageRequestDto;
import com.example.common.dto.ChatMessageResponseDto;
import com.example.common.enums.DatabaseOperation;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.ChatMessageMapper;
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
        return responseDto;
    }
}
