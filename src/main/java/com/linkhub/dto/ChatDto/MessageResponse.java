package com.linkhub.dto.ChatDto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {

    private Long id;

    private Long chatId;

    private Long senderId;

    private String senderName;

    private String content;

    private Boolean read;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}