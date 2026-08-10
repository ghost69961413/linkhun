package com.linkhub.controller;

import com.linkhub.dto.PostDto.PostRequest;
import com.linkhub.dto.PostDto.PostResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @Valid @RequestBody PostRequest request) {

        PostResponse response =
                postService.createPost(request);

        return ResponseEntity.ok(
                ApiResponse.<PostResponse>builder()
                        .success(true)
                        .message("Post created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<PostResponse> response =
                postService.getMyPosts(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<PostResponse>>builder()
                        .success(true)
                        .message("Your posts fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<PostResponse> response =
                postService.getFeed(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<PostResponse>>builder()
                        .success(true)
                        .message("Feed fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @PathVariable Long postId) {

        PostResponse response =
                postService.getPost(postId);

        return ResponseEntity.ok(
                ApiResponse.<PostResponse>builder()
                        .success(true)
                        .message("Post fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest request) {

        PostResponse response =
                postService.updatePost(postId, request);

        return ResponseEntity.ok(
                ApiResponse.<PostResponse>builder()
                        .success(true)
                        .message("Post updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId) {

        postService.deletePost(postId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Post deleted successfully")
                        .build()
        );
    }

}