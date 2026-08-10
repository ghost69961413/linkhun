package com.linkhub.repository;

import com.linkhub.entity.Post;
import com.linkhub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByUserAndDeletedFalse(
            User user,
            Pageable pageable
    );

    Page<Post> findByDeletedFalse(
            Pageable pageable
    );
    Page<Post> findByContentContainingIgnoreCase(
            String content,
            Pageable pageable
    );

    @Query("""
        SELECT p
        FROM Post p
        WHERE p.deleted = false
        AND (
            p.user = :currentUser
            OR p.user.id IN (
                SELECT f.following.id
                FROM Follow f
                WHERE f.follower = :currentUser
            )
        )
        ORDER BY p.createdAt DESC
        """)
    Page<Post> findFeedPosts(
            @Param("currentUser") User currentUser,
            Pageable pageable
    );
}