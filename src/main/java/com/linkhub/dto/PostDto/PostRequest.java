package com.linkhub.dto.PostDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {

    @NotBlank(message = "Post content is required")
    @Size(max = 5000, message = "Post content cannot exceed 5000 characters")
    private String content;

    private String imageUrl;

    private String videoUrl;

    private String visibility;
}