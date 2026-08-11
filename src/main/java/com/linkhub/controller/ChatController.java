package com.linkhub.controller;

import com.linkhub.dto.ChatDto.ChatRequest;
import com.linkhub.dto.ChatDto.ChatResponse;
import com.linkhub.dto.ChatDto.MessageRequest;
import com.linkhub.dto.ChatDto.MessageResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // =====================================================
    // CREATE / GET EXISTING CHAT
    // =====================================================

    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> createChat(
            @Valid @RequestBody ChatRequest request) {

        ChatResponse response =
                chatService.createChat(request);

        return ResponseEntity.ok(
                ApiResponse.<ChatResponse>builder()
                        .success(true)
                        .message("Chat created successfully")
                        .data(response)
                        .build()
        );
    }

    // =====================================================
    // GET MY CHATS
    // =====================================================

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getMyChats() {

        List<ChatResponse> response =
                chatService.getMyChats();

        return ResponseEntity.ok(
                ApiResponse.<List<ChatResponse>>builder()
                        .success(true)
                        .message("Chats fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // =====================================================
    // GET CHAT BY ID
    // =====================================================

    @GetMapping("/{chatId}")
    public ResponseEntity<ApiResponse<ChatResponse>> getChat(
            @PathVariable Long chatId) {

        ChatResponse response =
                chatService.getChat(chatId);

        return ResponseEntity.ok(
                ApiResponse.<ChatResponse>builder()
                        .success(true)
                        .message("Chat fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // =====================================================
    // SEND MESSAGE
    // =====================================================

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @PathVariable Long chatId,
            @Valid @RequestBody MessageRequest request) {

        MessageResponse response =
                chatService.sendMessage(
                        chatId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.<MessageResponse>builder()
                        .success(true)
                        .message("Message sent successfully")
                        .data(response)
                        .build()
        );
    }

    // =====================================================
    // GET MESSAGES
    // =====================================================

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>>
    getMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").ascending()
        );

        Page<MessageResponse> response =
                chatService.getMessages(
                        chatId,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.<Page<MessageResponse>>builder()
                        .success(true)
                        .message("Messages fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // =====================================================
    // MARK MESSAGES AS READ
    // =====================================================

    @PatchMapping("/{chatId}/messages/read")
    public ResponseEntity<ApiResponse<Void>>
    markMessagesAsRead(
            @PathVariable Long chatId) {

        chatService.markMessagesAsRead(chatId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Messages marked as read")
                        .build()
        );
    }

    // =====================================================
    // DELETE CHAT
    // =====================================================

    @DeleteMapping("/{chatId}")
    public ResponseEntity<ApiResponse<Void>>
    deleteChat(
            @PathVariable Long chatId) {

        chatService.deleteChat(chatId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Chat deleted successfully")
                        .build()
        );
    }
}