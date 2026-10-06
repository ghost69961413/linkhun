package com.linkhub.dto.ChatDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {

    @NotBlank(message = "Message content is required")
    @Size(max = 5000, message = "Message content cannot exceed 5000 characters")
    private String content;
}
