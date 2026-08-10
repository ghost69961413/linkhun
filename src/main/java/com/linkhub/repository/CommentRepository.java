package com.linkhub.repository;

import com.linkhub.entity.Comment;
import com.linkhub.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPost(Post post, Pageable pageable);

    long countByPost(Post post);
}