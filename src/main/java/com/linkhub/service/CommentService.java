package com.linkhub.service;

import com.linkhub.dto.CommentDto.CommentRequest;
import com.linkhub.dto.CommentDto.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentResponse addComment(Long postId, CommentRequest request);

    Page<CommentResponse> getComments(Long postId, Pageable pageable);

    CommentResponse updateComment(Long commentId, CommentRequest request);

    void deleteComment(Long commentId);
}