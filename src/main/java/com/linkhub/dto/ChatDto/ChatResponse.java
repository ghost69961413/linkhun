package com.linkhub.dto.ChatDto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {

    private Long id;

    private List<Long> participantIds;

    private List<String> participantNames;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}