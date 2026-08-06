package com.linkhub.service;

import com.linkhub.dto.FollowDto.FollowResponse;

import java.util.List;

public interface FollowService {

    void followUser(Long userId);

    void unfollowUser(Long userId);

    List<FollowResponse> getFollowers(Long userId);

    List<FollowResponse> getFollowing(Long userId);

}