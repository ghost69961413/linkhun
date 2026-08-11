package com.linkhub.service;

import com.linkhub.dto.ChatDto.ChatRequest;
import com.linkhub.dto.ChatDto.ChatResponse;
import com.linkhub.dto.ChatDto.MessageRequest;
import com.linkhub.dto.ChatDto.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

    // Chat
    ChatResponse createChat(ChatRequest request);

    List<ChatResponse> getMyChats();

    ChatResponse getChat(Long chatId);

    void deleteChat(Long chatId);

    // Messages
    MessageResponse sendMessage(
            Long chatId,
            MessageRequest request
    );

    Page<MessageResponse> getMessages(
            Long chatId,
            Pageable pageable
    );

    void markMessagesAsRead(Long chatId);
}