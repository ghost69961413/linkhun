package com.linkhub.dto.RepositoryDto;

import com.linkhub.enums.RepositoryVisibility;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepositoryRequest {

    @NotBlank(message = "Repository name is required")
    private String name;

    private String description;

    @NotBlank(message = "Repository URL is required")
    private String repositoryUrl;

    private String language;

    private RepositoryVisibility visibility;
}