package com.linkhub.service.impl;

import com.linkhub.dto.CommentDto.CommentRequest;
import com.linkhub.dto.CommentDto.CommentResponse;
import com.linkhub.entity.Comment;
import com.linkhub.entity.Post;
import com.linkhub.entity.User;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.CommentMapper;
import com.linkhub.repository.CommentRepository;
import com.linkhub.repository.PostRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.CommentService;
import com.linkhub.service.NotificationService;
import com.linkhub.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;
    private final PostService postService;

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public CommentResponse addComment(
            Long postId,
            CommentRequest request) {

        User user = getCurrentUser();
        postService.assertCanView(postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new BadRequestException("Post not found"));

        if (Boolean.TRUE.equals(post.getDeleted())) {
            throw new BadRequestException(
                    "Cannot comment on a deleted post");
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .user(user)
                .build();

        Comment savedComment = commentRepository.save(comment);

        if (!post.getUser().getId().equals(user.getId())) {

            notificationService.createNotification(
                    post.getUser().getId(),
                    user.getId(),
                    "COMMENT",
                    user.getFirstName() + " commented on your post",
                    post.getId()
            );
        }

        return commentMapper.toResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(
            Long postId,
            Pageable pageable) {

        postService.assertCanView(postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new BadRequestException("Post not found"));

        if (Boolean.TRUE.equals(post.getDeleted())) {
            throw new BadRequestException(
                    "Post has been deleted");
        }

        return commentRepository
                .findByPost(post, pageable)
                .map(commentMapper::toResponse);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(
            Long commentId,
            CommentRequest request) {

        User currentUser = getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new BadRequestException("Comment not found"));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException(
                    "You can only update your own comment");
        }

        comment.setContent(request.getContent());

        return commentMapper.toResponse(
                commentRepository.save(comment)
        );
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {

        User currentUser = getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new BadRequestException("Comment not found"));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException(
                    "You can only delete your own comment");
        }

        commentRepository.delete(comment);
    }
}
