package com.linkhub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends BaseEntity {


    @Column(nullable = false)
    private String title;

    @Column(length = 3000)
    private String description;

    private String githubUrl;

    private String liveDemoUrl;

    private String thumbnailUrl;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private String visibility;

    @Builder.Default
    private Boolean featured = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToMany
    @JoinTable(
            name = "project_technologies",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    @Builder.Default
    private Set<Technology> technologies = new HashSet<>();


}