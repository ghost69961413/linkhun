package com.linkhub.dto.SearchDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSearchResponse {

    private Long id;

    private String content;

    private String imageUrl;

    private String videoUrl;

    private String visibility;

    private Long viewCount;

    private Long userId;

    private String username;

    private String firstName;
}