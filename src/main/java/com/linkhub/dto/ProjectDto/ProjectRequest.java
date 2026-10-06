package com.linkhub.dto.ProjectDto;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequest {

    private String title;

    private String description;

    private String githubUrl;

    private Long linkhubRepositoryId;

    private String liveDemoUrl;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private String visibility;

    private Boolean featured;

    private Set<String> technologies;
    private Set<String> features;
    private Set<String> teamMembers;

}
