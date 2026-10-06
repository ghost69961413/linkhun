package com.linkhub.dto.RepositoryDto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RepositoryFileResponse {
    private Long id;
    private String path;
    private Boolean directory;
    private Boolean binary;
    private String mimeType;
    private Long size;
    private LocalDateTime updatedAt;
    private String content;
}
