package com.linkhub.controller;

import com.linkhub.dto.FollowDto.FollowResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> followUser(
            @PathVariable Long userId) {

        followService.followUser(userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("User followed successfully")
                        .build()
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(
            @PathVariable Long userId) {

        followService.unfollowUser(userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("User unfollowed successfully")
                        .build()
        );
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<List<FollowResponse>>> getFollowers(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                ApiResponse.<List<FollowResponse>>builder()
                        .success(true)
                        .message("Followers fetched successfully")
                        .data(followService.getFollowers(userId))
                        .build()
        );
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<ApiResponse<List<FollowResponse>>> getFollowing(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                ApiResponse.<List<FollowResponse>>builder()
                        .success(true)
                        .message("Following fetched successfully")
                        .data(followService.getFollowing(userId))
                        .build()
        );
    }
}