package com.example.mapper;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Component;
import com.example.common.dto.ChatParticipantResponseDto;
import com.example.common.dto.ChatResponseDto;
import com.example.model.Case;
import com.example.model.Chat;
import com.example.model.User;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ChatMapper {
    private final UserMapper userMapper;

    public Chat toChat(Case chatCase, List<User> participants, Instant lastMessageAt) {
        Chat chat = new Chat();
        chat.setChatCase(chatCase);
        chat.setParticipants(new HashSet<>(participants));
        chat.setLastMessageAt(lastMessageAt);
        return chat;
    }

    public ChatResponseDto toDto(Chat chat) {
        List<ChatParticipantResponseDto> participants = chat.getParticipants().stream()
                .map(userMapper::toChatParticipantDto)
                .toList();
        return new ChatResponseDto(chat.getId(), participants);
    }
}
