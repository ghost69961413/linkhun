package com.linkhub.dto.connection;

import com.linkhub.enums.ConnectionStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ConnectionRequestDto {
    Long requestId;
    ConnectionStatus status;
    LocalDateTime createdAt;
    ConnectionUserDto sender;
    ConnectionUserDto recipient;
}
