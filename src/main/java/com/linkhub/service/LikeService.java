package com.linkhub.service;

import com.linkhub.dto.LikeDto.LikeResponse;

public interface LikeService {

    LikeResponse likePost(Long postId);

    LikeResponse unlikePost(Long postId);

}