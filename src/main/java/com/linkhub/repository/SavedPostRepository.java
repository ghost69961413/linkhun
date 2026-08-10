package com.linkhub.repository;

import com.linkhub.entity.Post;
import com.linkhub.entity.SavedPost;
import com.linkhub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedPostRepository
        extends JpaRepository<SavedPost, Long> {

    boolean existsByUserAndPost(User user, Post post);

    Optional<SavedPost> findByUserAndPost(User user, Post post);

    void deleteByUserAndPost(User user, Post post);

    Page<SavedPost> findByUser(User user, Pageable pageable);
}