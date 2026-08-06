package com.linkhub.dto.ProjectDto;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

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

    private String liveDemoUrl;

    private String thumbnailUrl;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private String visibility;

    private Boolean featured;

    private Set<String> technologies;

}