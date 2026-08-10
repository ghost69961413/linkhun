package com.linkhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "saved_posts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_saved_post",
                        columnNames = {"user_id", "post_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}