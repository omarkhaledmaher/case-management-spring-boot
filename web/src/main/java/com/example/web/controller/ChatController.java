package com.example.web.controller;

import java.net.URI;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/cases/{caseId}/chats")
@AllArgsConstructor
@Tag(name = "Chats", description = "Operations related to case chats and messaging")
public class ChatController {
    private final ChatService chatService;
    private final ChatMessageService chatMessageService;

    @Operation(summary = "Gets chat by ID", description = "User must be a participant in the chat")
    @GetMapping("/{chatId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ChatResponseDto> getChat(@PathVariable Long caseId, @PathVariable Long chatId) {
        ChatResponseDto chat = chatService.getChatById(chatId);
        return ResponseEntity.ok(chat);
    }

    @Operation(summary = "Gets all chats for a case",
            description = "Returns all chats for the specified case that the user is a participant in with optional pagination")
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ChatResponseDto>> getAllChats(@PathVariable Long caseId,
            @ParameterObject Pageable pageable) {
        List<ChatResponseDto> chats = chatService.getAllChatsByCaseId(caseId, pageable);
        return ResponseEntity.ok(chats);
    }

    @Operation(summary = "Create a new chat",
            description = "Creates a new chat for the specified case with the given participants")
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ChatResponseDto> createChat(@PathVariable Long caseId, @Valid @RequestBody ChatRequestDto dto,
            UriComponentsBuilder ucb) {
        ChatResponseDto createdChat = chatService.createChat(caseId, dto.participantIds());
        URI location = ucb.path("/api/cases/{caseId}/chats/{id}").buildAndExpand(caseId, createdChat.id()).toUri();

        return ResponseEntity.created(location).body(createdChat);
    }

    @Operation(summary = "Sends a message to the specified chat",
            description = "User must be a participant in the chat")
    @MessageMapping("/chat/{chatId}/send")
    @SendTo("/topic/chat/{chatId}")
    public ChatMessageResponseDto createMessage(@DestinationVariable Long chatId,
            @Valid @Payload ChatMessageRequestDto dto) {

        return chatMessageService.createChatMessage(chatId, dto);
    }
}
