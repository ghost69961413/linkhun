package com.linkhub.repository;

import com.linkhub.entity.Like;
import com.linkhub.entity.Post;
import com.linkhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByPostAndUser(Post post, User user);

    Optional<Like> findByPostAndUser(Post post, User user);

    void deleteByPostAndUser(Post post, User user);

    long countByPost(Post post);
}