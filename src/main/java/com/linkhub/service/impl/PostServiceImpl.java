package com.linkhub.service.impl;

import com.linkhub.dto.PostDto.PostRequest;
import com.linkhub.dto.PostDto.PostResponse;
import com.linkhub.entity.Post;
import com.linkhub.entity.User;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.PostMapper;
import com.linkhub.repository.PostRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.MediaService;
import com.linkhub.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final MediaService mediaService;


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
    public PostResponse createPost(
            PostRequest request,
            MultipartFile image,
            MultipartFile video) {

        User user = getCurrentUser();

        String imageUrl = request.getImageUrl();
        String videoUrl = request.getVideoUrl();


        // =========================
        // IMAGE UPLOAD
        // =========================

        if (image != null && !image.isEmpty()) {

            imageUrl = mediaService.uploadImage(
                    image,
                    "linkhub/posts"
            );
        }


        // =========================
        // VIDEO UPLOAD
        // =========================

        if (video != null && !video.isEmpty()) {

            videoUrl = mediaService.uploadVideo(
                    video,
                    "linkhub/posts"
            );
        }


        // =========================
        // CREATE POST
        // =========================

        Post post = Post.builder()
                .content(request.getContent())
                .imageUrl(imageUrl)
                .videoUrl(videoUrl)
                .visibility(
                        request.getVisibility() != null
                                ? request.getVisibility()
                                : "PUBLIC"
                )
                .viewCount(0L)
                .deleted(false)
                .user(user)
                .build();


        Post savedPost =
                postRepository.save(post);

        return postMapper.toResponse(savedPost);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getMyPosts(
            Pageable pageable) {

        User user = getCurrentUser();

        return postRepository
                .findByUserAndDeletedFalse(
                        user,
                        pageable
                )
                .map(postMapper::toResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getFeed(Pageable pageable) {

        User currentUser = getCurrentUser();

        return postRepository
                .findFeedPosts(currentUser, pageable)
                .map(postMapper::toResponse);
    }


    @Override
    @Transactional
    public PostResponse getPost(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Post not found"
                        ));

        if (Boolean.TRUE.equals(post.getDeleted())) {

            throw new BadRequestException(
                    "Post has been deleted"
            );
        }

        post.setViewCount(
                post.getViewCount() + 1
        );

        Post updatedPost =
                postRepository.save(post);

        return postMapper.toResponse(updatedPost);
    }


    @Override
    @Transactional
    public PostResponse updatePost(
            Long postId,
            PostRequest request) {

        User currentUser = getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Post not found"
                        ));

        if (Boolean.TRUE.equals(post.getDeleted())) {

            throw new BadRequestException(
                    "Post has been deleted"
            );
        }

        if (!post.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new BadRequestException(
                    "You can only update your own post"
            );
        }

        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        post.setVideoUrl(request.getVideoUrl());

        if (request.getVisibility() != null) {

            post.setVisibility(
                    request.getVisibility()
            );
        }

        return postMapper.toResponse(
                postRepository.save(post)
        );
    }


    @Override
    @Transactional
    public void deletePost(Long postId) {

        User currentUser = getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Post not found"
                        ));

        if (!post.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new BadRequestException(
                    "You can only delete your own post"
            );
        }

        // Soft delete
        post.setDeleted(true);

        postRepository.save(post);
    }
}