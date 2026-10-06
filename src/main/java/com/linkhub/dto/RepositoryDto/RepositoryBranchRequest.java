package com.linkhub.dto.RepositoryDto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RepositoryBranchRequest {
    @NotBlank(message = "Branch name is required")
    private String name;
    private String fromBranch;
}
