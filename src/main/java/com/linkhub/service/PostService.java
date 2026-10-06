package com.linkhub.service;

import com.linkhub.dto.PostDto.PostRequest;
import com.linkhub.dto.PostDto.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface PostService {

    PostResponse createPost(
            PostRequest request,
            MultipartFile image,
            MultipartFile video
    );
    Page<PostResponse> getMyPosts(Pageable pageable);

    Page<PostResponse> getFeed(Pageable pageable);

    Page<PostResponse> getUserPosts(String username, Pageable pageable);

    Resource getPostMedia(Long postId, String kind);

    void assertCanView(Long postId);

    PostResponse getPost(Long postId);

    PostResponse updatePost(Long postId, PostRequest request);

    void deletePost(Long postId);


}
