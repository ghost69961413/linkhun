package com.linkhub.dto.ProjectDto;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.List;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;

    private String title;

    private String description;

    private String githubUrl;

    private Long linkhubRepositoryId;
    private String linkhubRepositoryPath;

    private String liveDemoUrl;

    private String thumbnailUrl;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private String visibility;

    private Boolean featured;

    private Set<String> technologies;
    private Set<String> features;
    private Set<String> teamMembers;
    private List<String> screenshots;
    private ProjectOwnerResponse owner;
    private Long ownerId;
    private String ownerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ProjectOwnerResponse {
        private Long id;
        private String userId;
        private String username;
        private String fullName;
        private String profilePicture;
    }

}
