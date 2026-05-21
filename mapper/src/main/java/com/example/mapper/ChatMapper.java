package com.example.mapper;

import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Component;
import com.example.common.dto.ChatMessageResponseDto;
import com.example.common.dto.ChatParticipantDto;
import com.example.common.dto.ChatResponseDto;
import com.example.model.Case;
import com.example.model.Chat;
import com.example.model.ChatMessage;
import com.example.model.User;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ChatMapper {
    private final UserMapper userMapper;
    private final ChatMessageMapper messageMapper;

    public Chat toChat(Case chatCase, List<User> participants, List<ChatMessage> messages) {
        Chat chat = new Chat();
        chat.setChatCase(chatCase);
        chat.setParticipants(new HashSet<>(participants));
        chat.setMessages(new HashSet<>(messages));
        return chat;
    }

    public ChatResponseDto toDto(Chat chat) {
        List<ChatParticipantDto> participants = chat.getParticipants().stream()
                .map(userMapper::toChatParticipantDto)
                .toList();
        List<ChatMessageResponseDto> messageContents = chat.getMessages().stream()
                .map(messageMapper::toDto).toList();

        return new ChatResponseDto(chat.getId(), participants, messageContents);
    }
}
