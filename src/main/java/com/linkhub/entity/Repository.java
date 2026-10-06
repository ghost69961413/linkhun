package com.linkhub.entity;

import com.linkhub.enums.RepositoryVisibility;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "repositories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repository extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "repository_url", nullable = true, length = 1000)
    private String repositoryUrl;

    private String language;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RepositoryVisibility visibility =
            RepositoryVisibility.PUBLIC;

    @Builder.Default private Boolean initializeReadme = true;
    private String license;
    @Column(length = 100) private String gitignoreTemplate;
    @Builder.Default @Column(length = 100) private String defaultBranch = "main";
    @Builder.Default private Integer stars = 0;
    @Builder.Default private Integer forks = 0;
    @Builder.Default @Column(name = "file_count", nullable = false) private Integer filesCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private java.util.List<RepositoryBranch> branches = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private java.util.List<RepositoryFile> files = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private java.util.List<RepositoryCommit> commits = new java.util.ArrayList<>();
}
