package com.linkhub.service.impl;

import com.linkhub.dto.SavedPostDto.SavedPostResponse;
import com.linkhub.entity.Post;
import com.linkhub.entity.SavedPost;
import com.linkhub.entity.User;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.SavedPostMapper;
import com.linkhub.repository.PostRepository;
import com.linkhub.repository.SavedPostRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.SavedPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavedPostServiceImpl implements SavedPostService {

    private final SavedPostRepository savedPostRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SavedPostMapper savedPostMapper;

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
    public SavedPostResponse savePost(Long postId) {

        User user = getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new BadRequestException("Post not found"));

        if (Boolean.TRUE.equals(post.getDeleted())) {
            throw new BadRequestException(
                    "Cannot save a deleted post");
        }

        if (savedPostRepository.existsByUserAndPost(user, post)) {
            throw new BadRequestException(
                    "Post is already saved");
        }

        SavedPost savedPost = SavedPost.builder()
                .user(user)
                .post(post)
                .build();

        return savedPostMapper.toResponse(
                savedPostRepository.save(savedPost)
        );
    }

    @Override
    @Transactional
    public void unsavePost(Long postId) {

        User user = getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new BadRequestException("Post not found"));

        SavedPost savedPost =
                savedPostRepository.findByUserAndPost(user, post)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Post is not saved"));

        savedPostRepository.delete(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SavedPostResponse> getMySavedPosts(
            Pageable pageable) {

        User user = getCurrentUser();

        return savedPostRepository
                .findByUser(user, pageable)
                .map(savedPostMapper::toResponse);
    }
}