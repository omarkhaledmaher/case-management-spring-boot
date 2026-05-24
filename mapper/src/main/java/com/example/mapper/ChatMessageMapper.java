package com.example.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.common.dto.ChatMessageRequestDto;
import com.example.common.dto.ChatMessageResponseDto;
import com.example.common.dto.ChatParticipantResponseDto;
import com.example.model.Chat;
import com.example.model.ChatMessage;
import com.example.model.User;

@Component
public class ChatMessageMapper {
    @Autowired
    private UserMapper userMapper;

    public ChatMessageResponseDto toDto(ChatMessage chatMessage) {
        ChatParticipantResponseDto senderDto = userMapper.toChatParticipantDto(chatMessage.getSender());
        return new ChatMessageResponseDto(
                chatMessage.getId(),
                senderDto,
                chatMessage.getText(),
                chatMessage.getTimestamp());
    }

    public ChatMessage toChatMessage(ChatMessageRequestDto dto, User sender, Chat chat) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setText(dto.text());
        chatMessage.setChat(chat);
        return chatMessage;
    }
}
