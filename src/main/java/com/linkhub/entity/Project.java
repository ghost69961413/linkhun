package com.linkhub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linkhub_repository_id")
    private Repository linkedRepository;

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

    @ElementCollection
    @CollectionTable(name = "project_features", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "feature", length = 500)
    @Builder.Default
    private Set<String> features = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "project_team_members", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "member", length = 255)
    @Builder.Default
    private Set<String> teamMembers = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "project_screenshots", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "image_url", length = 2000)
    @Builder.Default
    private List<String> screenshots = new ArrayList<>();


}
