package com.linkhub.service.impl;

import com.linkhub.dto.PostDto.PostRequest;
import com.linkhub.dto.PostDto.PostResponse;
import com.linkhub.entity.Post;
import com.linkhub.entity.User;
import com.linkhub.enums.ConnectionStatus;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.PostMapper;
import com.linkhub.repository.PostRepository;
import com.linkhub.repository.LikeRepository;
import com.linkhub.repository.CommentRepository;
import com.linkhub.repository.ConnectionRequestRepository;
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
import org.springframework.core.io.Resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ConnectionRequestRepository connectionRequestRepository;
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

        String visibility = normalizedVisibility(request.getVisibility());
        Post post = Post.builder()
                .content(request.getContent())
                .imageUrl(imageUrl)
                .videoUrl(videoUrl)
                .visibility(visibility)
                .viewCount(0L)
                .deleted(false)
                .user(user)
                .build();


        Post savedPost =
                postRepository.save(post);

        return toResponse(savedPost, user);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getMyPosts(
            Pageable pageable) {

        User user = getCurrentUser();

        return toResponsePage(
                postRepository.findByUserAndDeletedFalse(user, pageable),
                user
        );
    }


    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getFeed(Pageable pageable) {

        User currentUser = getCurrentUser();

        return toResponsePage(
                postRepository.findFeedPosts(currentUser, pageable),
                currentUser
        );
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

        User viewer = getCurrentUser();
        if (!canView(post, viewer)) {
            throw new BadRequestException("This post is not available to you");
        }

        post.setViewCount(
                post.getViewCount() + 1
        );

        Post updatedPost =
                postRepository.save(post);

        return toResponse(updatedPost, viewer);
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

            post.setVisibility(normalizedVisibility(request.getVisibility()));
        }

        return toResponse(postRepository.save(post), currentUser);
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

    private PostResponse toResponse(Post post, User currentUser) {
        PostResponse response = postMapper.toResponse(post);
        response.setLikeCount(likeRepository.countByPost(post));
        response.setCommentCount(commentRepository.countByPost(post));
        response.setLikedByMe(likeRepository.existsByPostAndUser(post, currentUser));
        return response;
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getUserPosts(String username, Pageable pageable) {
        User viewer = getCurrentUser();
        if (userRepository.findByUsername(username).isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        return toResponsePage(
                postRepository.findVisiblePostsByUsername(username, viewer, ConnectionStatus.ACCEPTED, pageable),
                viewer
        );
    }

    @Transactional(readOnly = true)
    public Resource getPostMedia(Long postId, String kind) {
        User viewer = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("Post not found"));
        if (Boolean.TRUE.equals(post.getDeleted()) || !canView(post, viewer)) {
            throw new BadRequestException("This post is not available to you");
        }
        String mediaUrl = "image".equalsIgnoreCase(kind) ? post.getImageUrl()
                : "video".equalsIgnoreCase(kind) ? post.getVideoUrl() : null;
        if (mediaUrl == null || mediaUrl.isBlank()) throw new BadRequestException("Post media was not found");
        return mediaService.load(mediaUrl);
    }

    @Transactional(readOnly = true)
    public void assertCanView(Long postId) {
        User viewer = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("Post not found"));
        if (Boolean.TRUE.equals(post.getDeleted()) || !canView(post, viewer)) {
            throw new BadRequestException("This post is not available to you");
        }
    }

    private String normalizedVisibility(String requested) {
        String value = requested == null || requested.isBlank() ? "PUBLIC" : requested.trim().toUpperCase();
        if (!value.equals("PUBLIC") && !value.equals("CONNECTIONS") && !value.equals("PRIVATE")) {
            throw new BadRequestException("Post visibility must be PUBLIC, CONNECTIONS, or PRIVATE");
        }
        return value;
    }

    private boolean canView(Post post, User viewer) {
        if (post.getUser().getId().equals(viewer.getId())) return true;
        String visibility = post.getVisibility() == null ? "PUBLIC" : post.getVisibility().toUpperCase();
        if (visibility.equals("PUBLIC")) return true;
        if (!visibility.equals("CONNECTIONS")) return false;
        return connectionRequestRepository.existsBySenderAndRecipientAndStatus(viewer, post.getUser(), ConnectionStatus.ACCEPTED)
                || connectionRequestRepository.existsBySenderAndRecipientAndStatus(post.getUser(), viewer, ConnectionStatus.ACCEPTED);
    }

    private Page<PostResponse> toResponsePage(Page<Post> posts, User currentUser) {
        List<Long> postIds = posts.getContent().stream().map(Post::getId).toList();
        if (postIds.isEmpty()) return posts.map(postMapper::toResponse);

        Map<Long, Long> likeCounts = countByPostId(likeRepository.countByPostIds(postIds));
        Map<Long, Long> commentCounts = countByPostId(commentRepository.countByPostIds(postIds));
        Set<Long> likedPostIds = new HashSet<>(likeRepository.findLikedPostIdsByUser(postIds, currentUser));

        return posts.map(post -> {
            PostResponse response = postMapper.toResponse(post);
            response.setLikeCount(likeCounts.getOrDefault(post.getId(), 0L));
            response.setCommentCount(commentCounts.getOrDefault(post.getId(), 0L));
            response.setLikedByMe(likedPostIds.contains(post.getId()));
            return response;
        });
    }

    private Map<Long, Long> countByPostId(Collection<Object[]> rows) {
        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : rows) {
            counts.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
        }
        return counts;
    }
}
