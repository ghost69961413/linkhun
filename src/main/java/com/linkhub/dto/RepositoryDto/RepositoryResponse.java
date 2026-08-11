package com.linkhub.dto.RepositoryDto;

import com.linkhub.enums.RepositoryVisibility;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepositoryResponse {

    private Long id;

    private String name;

    private String description;

    private String repositoryUrl;

    private String language;

    private RepositoryVisibility visibility;

    private Long ownerId;

    private String ownerName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}