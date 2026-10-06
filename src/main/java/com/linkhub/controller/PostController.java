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
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @Valid @ModelAttribute PostRequest request,

            @RequestParam(value = "image", required = false)
            MultipartFile image,

            @RequestParam(value = "video", required = false)
            MultipartFile video) {

        PostResponse response =
                postService.createPost(
                        request,
                        image,
                        video
                );

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

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getUserPosts(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostResponse> response = postService.getUserPosts(username, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<PostResponse>>builder()
                .success(true)
                .message("Visible profile posts fetched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{postId}/media/{kind}")
    public ResponseEntity<Resource> getPostMedia(@PathVariable Long postId, @PathVariable String kind) {
        Resource media = postService.getPostMedia(postId, kind);
        MediaType contentType = MediaTypeFactory.getMediaType(media).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(contentType).body(media);
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
