package com.linkhub.controller;

import com.linkhub.dto.LikeDto.LikeResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping({"/{postId}/likes", "/{postId}/like"})
    public ResponseEntity<ApiResponse<LikeResponse>> likePost(
            @PathVariable Long postId) {

        LikeResponse response =
                likeService.likePost(postId);

        return ResponseEntity.ok(
                ApiResponse.<LikeResponse>builder()
                        .success(true)
                        .message("Post liked successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping({"/{postId}/likes", "/{postId}/like"})
    public ResponseEntity<ApiResponse<LikeResponse>> unlikePost(
            @PathVariable Long postId) {

        LikeResponse response =
                likeService.unlikePost(postId);

        return ResponseEntity.ok(
                ApiResponse.<LikeResponse>builder()
                        .success(true)
                        .message("Post unliked successfully")
                        .data(response)
                        .build()
        );
    }
}
