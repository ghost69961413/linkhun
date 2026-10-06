package com.linkhub.mapper;

import com.linkhub.dto.ChatDto.ChatResponse;
import com.linkhub.dto.ChatDto.MessageResponse;
import com.linkhub.entity.Chat;
import com.linkhub.entity.Message;
import com.linkhub.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatMapper {

    public ChatResponse toChatResponse(Chat chat) {

        List<User> participants =
                chat.getParticipants()
                        .stream()
                        .toList();

        return ChatResponse.builder()
                .id(chat.getId())
                .participantIds(
                        participants.stream()
                                .map(User::getId)
                                .toList()
                )
                .participantNames(
                        participants.stream()
                                .map(user -> (user.getFirstName() + " " + user.getLastName()).trim())
                                .toList()
                )
                .createdAt(chat.getCreatedAt())
                .updatedAt(chat.getUpdatedAt())
                .build();
    }


    public MessageResponse toMessageResponse(
            Message message) {

        User sender = message.getSender();

        return MessageResponse.builder()
                .id(message.getId())
                .chatId(
                        message.getChat() != null
                                ? message.getChat().getId()
                                : null
                )
                .senderId(
                        sender != null
                                ? sender.getId()
                                : null
                )
                .senderName(
                        sender != null
                                ? (sender.getFirstName() + " " + sender.getLastName()).trim()
                                : null
                )
                .content(message.getContent())
                .read(message.getRead())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}
