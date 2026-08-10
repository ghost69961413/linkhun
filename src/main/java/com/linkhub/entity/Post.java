package com.linkhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    private String videoUrl;

    @Builder.Default
    private String visibility = "PUBLIC";

    @Builder.Default
    private Long viewCount = 0L;

    @Builder.Default
    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}