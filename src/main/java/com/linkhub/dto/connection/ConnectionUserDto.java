package com.linkhub.dto.connection;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ConnectionUserDto {
    Long userId;
    String username;
    String fullName;
    String headline;
    String profileImage;
}
