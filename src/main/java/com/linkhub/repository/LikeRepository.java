package com.linkhub.repository;

import com.linkhub.entity.Like;
import com.linkhub.entity.Post;
import com.linkhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByPostAndUser(Post post, User user);

    Optional<Like> findByPostAndUser(Post post, User user);

    void deleteByPostAndUser(Post post, User user);

    long countByPost(Post post);

    @Query("SELECT l.post.id, COUNT(l) FROM Like l WHERE l.post.id IN :postIds GROUP BY l.post.id")
    List<Object[]> countByPostIds(@Param("postIds") Collection<Long> postIds);

    @Query("SELECT l.post.id FROM Like l WHERE l.post.id IN :postIds AND l.user = :user")
    List<Long> findLikedPostIdsByUser(@Param("postIds") Collection<Long> postIds, @Param("user") User user);
}
