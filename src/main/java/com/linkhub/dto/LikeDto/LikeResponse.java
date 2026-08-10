package com.linkhub.dto.LikeDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponse {

    private Long postId;

    private Long userId;

    private boolean liked;

    private long likeCount;
}