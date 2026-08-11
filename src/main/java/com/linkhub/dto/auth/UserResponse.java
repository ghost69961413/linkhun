package com.linkhub.dto.UserDto;

import com.linkhub.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private Role role;

    private Boolean accountVerified;

    private String profilePicture;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}