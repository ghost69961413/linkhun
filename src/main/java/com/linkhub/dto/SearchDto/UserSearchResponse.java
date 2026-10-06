package com.linkhub.dto.SearchDto;

import lombok.*;
import com.linkhub.enums.ProfileType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchResponse {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String profilePicture;

    private String headline;
    private ProfileType profileType;
}
