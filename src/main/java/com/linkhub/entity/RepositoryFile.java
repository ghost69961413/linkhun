package com.linkhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "repository_files", uniqueConstraints = @UniqueConstraint(name = "uk_repository_branch_path", columnNames = {"branch_id", "path"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RepositoryFile extends BaseEntity {
    @Column(nullable = false, length = 1000)
    private String path;
    @Builder.Default @Column(nullable = false)
    private Boolean directory = false;
    @Lob @Column(columnDefinition = "LONGTEXT")
    private String content;
    @Builder.Default @Column(name = "is_binary", nullable = false)
    private Boolean binary = false;
    @Column(name = "mime_type", length = 150)
    private String mimeType;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private RepositoryBranch branch;
}
