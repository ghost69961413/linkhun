package com.linkhub.dto.RepositoryDto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RepositoryCommitResponse {
    private Long id;
    private String message;
    private String changeSummary;
    private String branch;
    private String author;
    private LocalDateTime createdAt;
}
