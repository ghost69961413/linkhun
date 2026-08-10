package com.linkhub.dto.CommentDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 2000, message = "Comment cannot exceed 2000 characters")
    private String content;
}