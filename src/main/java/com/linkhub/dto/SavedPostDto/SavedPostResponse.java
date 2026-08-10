package com.linkhub.dto.SavedPostDto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedPostResponse {

    private Long id;

    private Long postId;

    private String content;

    private String imageUrl;

    private String username;

    private LocalDateTime savedAt;
}