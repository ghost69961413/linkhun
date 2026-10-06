package com.linkhub.repository;

import com.linkhub.entity.Post;
import com.linkhub.entity.User;
import com.linkhub.enums.ConnectionStatus;
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

    @Query("""
        SELECT p
        FROM Post p
        WHERE p.deleted = false
          AND p.user.username = :username
          AND (
              p.user = :viewer
              OR UPPER(p.visibility) = 'PUBLIC'
              OR (
                  UPPER(p.visibility) = 'CONNECTIONS'
                  AND EXISTS (
                      SELECT cr.id
                      FROM ConnectionRequest cr
                      WHERE cr.status = :accepted
                        AND ((cr.sender = p.user AND cr.recipient = :viewer)
                          OR (cr.recipient = p.user AND cr.sender = :viewer))
                  )
              )
          )
        ORDER BY p.createdAt DESC
        """)
    Page<Post> findVisiblePostsByUsername(
            @Param("username") String username,
            @Param("viewer") User viewer,
            @Param("accepted") ConnectionStatus accepted,
            Pageable pageable
    );

    @Query("""
        SELECT p
        FROM Post p
        WHERE p.deleted = false
          AND LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))
          AND (
              p.user = :viewer
              OR UPPER(p.visibility) = 'PUBLIC'
              OR (
                  UPPER(p.visibility) = 'CONNECTIONS'
                  AND EXISTS (
                      SELECT cr.id
                      FROM ConnectionRequest cr
                      WHERE cr.status = :accepted
                        AND ((cr.sender = p.user AND cr.recipient = :viewer)
                          OR (cr.recipient = p.user AND cr.sender = :viewer))
                  )
              )
          )
        ORDER BY p.createdAt DESC
        """)
    Page<Post> findVisiblePostsByContent(
            @Param("query") String query,
            @Param("viewer") User viewer,
            @Param("accepted") ConnectionStatus accepted,
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
            OR UPPER(p.visibility) = 'PUBLIC'
            OR (
                UPPER(p.visibility) = 'CONNECTIONS'
                AND EXISTS (
                    SELECT cr.id
                    FROM ConnectionRequest cr
                    WHERE cr.status = com.linkhub.enums.ConnectionStatus.ACCEPTED
                    AND ((cr.sender = p.user AND cr.recipient = :currentUser)
                      OR (cr.recipient = p.user AND cr.sender = :currentUser))
                )
            )
        )
        ORDER BY p.createdAt DESC
        """)
    Page<Post> findFeedPosts(
            @Param("currentUser") User currentUser,
            Pageable pageable
    );
}
