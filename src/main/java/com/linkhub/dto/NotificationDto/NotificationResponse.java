package com.linkhub.dto.NotificationDto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;

    private String type;

    private String message;

    private Long senderId;

    private String senderUsername;

    private Long referenceId;

    private Boolean isRead;

    private LocalDateTime createdAt;
}