package com.example.web.controller;

import java.net.URI;
import java.util.List;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Tag(name = "Chats", description = "Operations related to case chats and messaging")
public class ChatController {
    private final ChatService chatService;
    private final ChatMessageService chatMessageService;

    @Operation(summary = "Gets chat by ID", description = "User must be a participant in the chat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chat found and returned"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403", description = "Missing USER or ADMIN role or not a participant in chat"),
            @ApiResponse(responseCode = "404", description = "Case or Chat with specified ID not found")
    })
    @GetMapping("chats/{chatId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ChatResponseDto> getChat(@PathVariable Long chatId) {
        ChatResponseDto chat = chatService.getChatById(chatId);
        return ResponseEntity.ok(chat);
    }

    @Operation(summary = "Gets all chats for a case",
            description = "Returns all chats for the specified case that the user is a participant in with optional pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chats retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403", description = "Missing USER or ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Case with specified ID not found")
    })
    @GetMapping("cases/{caseId}/chats")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ChatResponseDto>> getAllCaseChats(@PathVariable Long caseId) {
        return ResponseEntity.ok(chatService.getAllChatsByCaseId(caseId));
    }

    @Operation(summary = "Create a new chat",
            description = "Creates a new chat for the specified case with the given participants")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chat created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or missing participant IDs"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403", description = "Missing USER or ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Case with specified ID not found")
    })
    @PostMapping("/cases/{caseId}/chats")
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

    @Operation(summary = "Gets all messages for a chat",
            description = "Returns all messages for the specified chat that the user is a participant in")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated session"),
            @ApiResponse(responseCode = "403", description = "Missing USER or ADMIN role or not a participant in chat"),
            @ApiResponse(responseCode = "404", description = "Chat with specified ID not found")
    })
    @GetMapping("chats/{chatId}/messages")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ChatMessageResponseDto>> getAllMessages(@PathVariable Long chatId) {
        return ResponseEntity.ok(chatMessageService.getAllMessagesByChatId(chatId));
    }
}
