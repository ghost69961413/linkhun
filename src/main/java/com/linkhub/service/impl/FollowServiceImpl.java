package com.linkhub.service.impl;

import com.linkhub.dto.FollowDto.FollowResponse;
import com.linkhub.entity.Follow;
import com.linkhub.entity.User;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.FollowMapper;
import com.linkhub.repository.FollowRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.FollowService;
import com.linkhub.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FollowMapper followMapper;
    private final NotificationService notificationService;

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    @Override
    public void followUser(Long userId) {

        User currentUser = getCurrentUser();

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (currentUser.getId().equals(targetUser.getId())) {
            throw new BadRequestException("You cannot follow yourself");
        }

        if (followRepository.existsByFollowerAndFollowing(currentUser, targetUser)) {
            throw new BadRequestException("Already following this user");
        }

        Follow follow = Follow.builder()
                .follower(currentUser)
                .following(targetUser)
                .build();

        followRepository.save(follow);

        notificationService.createNotification(
                targetUser.getId(),
                currentUser.getId(),
                "FOLLOW",
                currentUser.getFirstName() + " started following you",
                currentUser.getId()
        );
    }

    @Override
    public void unfollowUser(Long userId) {

        User currentUser = getCurrentUser();

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        followRepository.deleteByFollowerAndFollowing(currentUser, targetUser);
    }

    @Override
    public List<FollowResponse> getFollowers(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        return followRepository.findByFollowing(user)
                .stream()
                .map(follow -> followMapper.toResponse(follow.getFollower()))
                .toList();
    }

    @Override
    public List<FollowResponse> getFollowing(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        return followRepository.findByFollower(user)
                .stream()
                .map(follow -> followMapper.toResponse(follow.getFollowing()))
                .toList();
    }
}