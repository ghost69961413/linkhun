package com.linkhub.dto.RepositoryDto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RepositoryFileRequest {
    @NotBlank(message = "File or folder path is required")
    private String path;
    private String content;
    private Boolean directory;
    private String commitMessage;
}
