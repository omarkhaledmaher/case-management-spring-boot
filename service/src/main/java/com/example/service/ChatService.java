package com.example.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.ChatMessageRequestDto;
import com.example.common.dto.ChatMessageResponseDto;
import com.example.common.dto.ChatResponseDto;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.ChatMapper;
import com.example.mapper.ChatMessageMapper;
import com.example.model.Case;
import com.example.model.Chat;
import com.example.model.ChatMessage;
import com.example.model.User;
import com.example.repository.CaseRepository;
import com.example.repository.ChatMessageRepository;
import com.example.repository.ChatRepository;
import com.example.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatMapper mapper;
    private final ChatRepository repository;
    private final ChatMessageMapper messageMapper;
    private final ChatMessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final CaseRepository caseRepository;

    public ChatResponseDto getChatById(Long chatId, String username) {
        Chat chat = chatRepository.findByIdAndParticipantsUsername(chatId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with id " + chatId + " not found"));

        return mapper.toDto(chat);
    }

    public List<ChatResponseDto> getAllChatsByCaseId(Long caseId, String username, Pageable pageable) {
        List<Chat> chats = repository.findByChatCaseIdAndParticipantsUsername(caseId, username, pageable);

        return chats.stream().map(mapper::toDto).toList();
    }

    @Transactional
    public ChatResponseDto createChat(Long caseId, List<Long> participantIds, String username) {
        Case chatCase = caseRepository.findByIdAndAssignedUsersUsername(caseId, username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Case with id " + caseId + " not found or you are not assigned to it"));

        List<User> participants = userRepository.findAllById(participantIds);

        List<User> caseUsers = chatCase.getAssignedUsers();
        if (!caseUsers.containsAll(participants)) {
            throw new IllegalArgumentException("One or more participants are not assigned to this case");
        }

        Chat createdChat = repository.save(mapper.toChat(chatCase, participants, new ArrayList<>()));
        return mapper.toDto(createdChat);
    }

    @Transactional
    public ChatMessageResponseDto createChatMessage(Long chatId, ChatMessageRequestDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found"));

        Chat chat = chatRepository.findByIdAndParticipants(chatId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with id " + chatId + " not found"));

        ChatMessage createdMessage = messageRepository.save(messageMapper.toChatMessage(dto, user, chat));
        messageRepository.flush();
        return messageMapper.toDto(createdMessage);
    }
}
