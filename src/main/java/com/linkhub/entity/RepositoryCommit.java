package com.linkhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "repository_commits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RepositoryCommit extends BaseEntity {
    @Column(nullable = false, length = 200)
    private String message;
    @Column(length = 2000)
    private String changeSummary;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private RepositoryBranch branch;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
