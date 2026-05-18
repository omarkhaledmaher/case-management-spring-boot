package com.example.web;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.example.service.ChatMessageService;
import com.example.service.ChatService;
import lombok.AllArgsConstructor;

@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@RestController
@RequestMapping("/api/cases/{caseId}/chats")
@AllArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatMessageService chatMessageService;

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponseDto> getChat(@PathVariable Long caseId, @PathVariable Long chatId) {
        ChatResponseDto chat = chatService.getChatById(chatId);
        return ResponseEntity.ok(chat);
    }

    @GetMapping
    public ResponseEntity<List<ChatResponseDto>> getAllChats(@PathVariable Long caseId, Pageable pageable) {
        List<ChatResponseDto> chats = chatService.getAllChatsByCaseId(caseId, pageable);
        return ResponseEntity.ok(chats);
    }

    @PostMapping
    public ResponseEntity<ChatResponseDto> createChat(@PathVariable Long caseId, @RequestBody ChatRequestDto dto,
                    UriComponentsBuilder ucb) {
        ChatResponseDto createdChat = chatService.createChat(caseId, dto.participantIds());
        URI location = ucb.path("/api/cases/{caseId}/chats/{id}").buildAndExpand(caseId, createdChat.id()).toUri();

        return ResponseEntity.created(location).body(createdChat);
    }

    @MessageMapping("/chat/{chatId}/send")
    @SendTo("/topic/chat/{chatId}")
    public ChatMessageResponseDto createMessage(@DestinationVariable Long chatId, @Payload ChatMessageRequestDto dto,
            Principal principal) {
        return chatMessageService.createChatMessage(chatId, dto, principal);
    }
}
