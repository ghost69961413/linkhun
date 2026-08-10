package com.linkhub.service;

import com.linkhub.dto.PostDto.PostRequest;
import com.linkhub.dto.PostDto.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    PostResponse createPost(PostRequest request);

    Page<PostResponse> getMyPosts(Pageable pageable);

    Page<PostResponse> getFeed(Pageable pageable);

    PostResponse getPost(Long postId);

    PostResponse updatePost(Long postId, PostRequest request);

    void deletePost(Long postId);


}