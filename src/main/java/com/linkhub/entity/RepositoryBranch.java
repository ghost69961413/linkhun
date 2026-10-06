package com.linkhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "repository_branches", uniqueConstraints = @UniqueConstraint(name = "uk_repo_branch_name", columnNames = {"repository_id", "name"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RepositoryBranch extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;
    @Builder.Default @Column(nullable = false)
    private Boolean defaultBranch = false;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;
}
