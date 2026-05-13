package com.example.web;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.common.dto.ChatMessageRequestDto;
import com.example.common.dto.ChatMessageResponseDto;
import com.example.common.dto.ChatRequestDto;
import com.example.common.dto.ChatResponseDto;
import com.example.security.CurrentUser;
import com.example.service.ChatService;

@RestController
@RequestMapping("/api/cases/{caseId}/chats")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponseDto> getChat(@PathVariable Long caseId, @PathVariable Long chatId,
            @CurrentUser String username) {
        ChatResponseDto chat = chatService.getChatById(chatId, username);
        return ResponseEntity.ok(chat);
    }

    @GetMapping
    public ResponseEntity<List<ChatResponseDto>> getAllChats(@PathVariable Long caseId,
            @CurrentUser String username, Pageable pageable) {
        List<ChatResponseDto> chats = chatService.getAllChatsByCaseId(caseId, username, pageable);
        return ResponseEntity.ok(chats);
    }

    @PostMapping("/{chatId}/message")
    public ResponseEntity<ChatMessageResponseDto> createMessage(@PathVariable Long chatId,
            @RequestBody ChatMessageRequestDto dto, @CurrentUser String username, UriComponentsBuilder ucb) {

        ChatMessageResponseDto message = chatService.createChatMessage(chatId, dto, username);
        URI location = ucb.path("/api/cases/{caseId}/chats/{id}").buildAndExpand(chatId, message.id()).toUri();
        return ResponseEntity.created(location).body(message);
    }

    @PostMapping
    public ResponseEntity<ChatResponseDto> createChat(@PathVariable Long caseId, @RequestBody ChatRequestDto dto,
            @CurrentUser String username,
            UriComponentsBuilder ucb) {
        ChatResponseDto createdChat = chatService.createChat(caseId, dto.participantIds(), username);
        URI location = ucb.path("/api/cases/{caseId}/chats/{id}").buildAndExpand(caseId, createdChat.id()).toUri();

        return ResponseEntity.created(location).body(createdChat);
    }
}
