package com.linkhub.dto.FollowDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResponse {

    private Long userId;

    private String username;

    private String fullName;

    private String profileImage;

}