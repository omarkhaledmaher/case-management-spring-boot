package com.example.service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.common.dto.ChatResponseDto;
import com.example.common.enums.DatabaseOperation;
import com.example.common.exceptions.ResourceNotFoundException;
import com.example.mapper.ChatMapper;
import com.example.model.Case;
import com.example.model.Chat;
import com.example.model.User;
import com.example.repository.CaseRepository;
import com.example.repository.ChatRepository;
import com.example.repository.UserRepository;
import com.example.security.IAuthFacade;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatMapper mapper;
    private final ChatRepository repository;
    private final UserRepository userRepository;
    private final CaseRepository caseRepository;
    private final EventPublisher eventPublisher;
    private final UserNotificationPublisher userNotificationPublisher;
    private final IAuthFacade authFacade;

    public ChatResponseDto getChatById(Long chatId) {
        String username = authFacade.getUsername();
        Chat chat = repository.findByIdAndParticipantsUsername(chatId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with id " + chatId + " not found"));

        return mapper.toDto(chat);
    }

    public List<ChatResponseDto> getAllChatsByCaseId(Long caseId) {
        String username = authFacade.getUsername();
        Sort sort = Sort.by(Sort.Direction.DESC, Chat::getLastMessageAt);
        List<Chat> chats = repository.findByChatCaseIdAndParticipantsUsername(caseId, username, sort);

        return chats.stream().map(mapper::toDto).toList();
    }

    @Transactional
    public ChatResponseDto createChat(Long caseId, List<Long> participantIds) {
        String username = authFacade.getUsername();
        Case chatCase = caseRepository.findByIdAndAssignedUsersUsername(caseId, username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Case with id " + caseId + " not found or you are not assigned to it"));

        List<User> participants = userRepository.findAllById(participantIds);

        Set<User> caseUsers = chatCase.getAssignedUsers();
        if (!caseUsers.containsAll(participants)) {
            throw new IllegalArgumentException("One or more participants are not assigned to this case");
        }

        Chat createdChat = repository.save(mapper.toChat(chatCase, participants, Instant.now()));
        ChatResponseDto responseDto = mapper.toDto(createdChat);
        eventPublisher.publishEvent(DatabaseOperation.CREATED, "Chat", "createChat", responseDto);
        publishChatNotification(participants, username, chatCase);

        return responseDto;
    }

    private void publishChatNotification(List<User> allParticipants, String username, Case chatCase) {
        List<String> participants =
                allParticipants.stream().map((u) -> u.getUsername()).filter(u -> !u.equals(username)).toList();

        userNotificationPublisher.publishUserNotification(
                "New chat created", "A new chat has been created for case " + chatCase.getName(),
                participants);
    }
}
