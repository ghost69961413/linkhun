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

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String repositoryUrl;

    private String language;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RepositoryVisibility visibility =
            RepositoryVisibility.PUBLIC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}