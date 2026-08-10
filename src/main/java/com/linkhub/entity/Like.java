package com.linkhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_post_user_like",
                        columnNames = {"post_id", "user_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}