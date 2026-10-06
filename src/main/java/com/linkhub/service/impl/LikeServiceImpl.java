package com.linkhub.service.impl;

import com.linkhub.dto.LikeDto.LikeResponse;
import com.linkhub.entity.Like;
import com.linkhub.entity.Post;
import com.linkhub.entity.User;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.LikeMapper;
import com.linkhub.repository.LikeRepository;
import com.linkhub.repository.PostRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.LikeService;
import com.linkhub.service.NotificationService;
import com.linkhub.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeMapper likeMapper;
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
    public LikeResponse likePost(Long postId) {

        User user = getCurrentUser();
        postService.assertCanView(postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new BadRequestException("Post not found"));

        if (Boolean.TRUE.equals(post.getDeleted())) {
            throw new BadRequestException("Post has been deleted");
        }

        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new BadRequestException(
                    "You have already liked this post"
            );
        }

        Like like = Like.builder()
                .post(post)
                .user(user)
                .build();

        Like savedLike = likeRepository.save(like);

        LikeResponse response = likeMapper.toResponse(savedLike);

        response.setLiked(true);
        response.setLikeCount(likeRepository.countByPost(post));

        if (!post.getUser().getId().equals(user.getId())) {

            notificationService.createNotification(
                    post.getUser().getId(),
                    user.getId(),
                    "LIKE",
                    user.getFirstName() + " liked your post",
                    post.getId()
            );
        }

        return response;
    }

    @Override
    @Transactional
    public LikeResponse unlikePost(Long postId) {

        User user = getCurrentUser();
        postService.assertCanView(postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new BadRequestException("Post not found"));

        if (Boolean.TRUE.equals(post.getDeleted())) {
            throw new BadRequestException("Post has been deleted");
        }

        if (!likeRepository.existsByPostAndUser(post, user)) {
            throw new BadRequestException(
                    "You have not liked this post"
            );
        }

        likeRepository.deleteByPostAndUser(post, user);

        LikeResponse response = LikeResponse.builder()
                .postId(post.getId())
                .userId(user.getId())
                .liked(false)
                .likeCount(likeRepository.countByPost(post))
                .build();

        return response;
    }
}
