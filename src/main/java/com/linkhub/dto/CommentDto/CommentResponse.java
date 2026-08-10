package com.linkhub.dto.CommentDto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;

    private String content;

    private Long postId;

    private Long userId;

    private String username;

    private String firstName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}