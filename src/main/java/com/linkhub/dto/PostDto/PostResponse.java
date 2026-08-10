package com.linkhub.dto.PostDto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

    private Long id;

    private String content;

    private String imageUrl;

    private String videoUrl;

    private String visibility;

    private Long viewCount;

    private Long userId;

    private String username;

    private String fullName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}