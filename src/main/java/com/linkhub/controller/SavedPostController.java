package com.linkhub.controller;

import com.linkhub.dto.SavedPostDto.SavedPostResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.SavedPostService;
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
public class SavedPostController {

    private final SavedPostService savedPostService;

    @PostMapping("/{postId}/save")
    public ResponseEntity<ApiResponse<SavedPostResponse>> savePost(
            @PathVariable Long postId) {

        SavedPostResponse response =
                savedPostService.savePost(postId);

        return ResponseEntity.ok(
                ApiResponse.<SavedPostResponse>builder()
                        .success(true)
                        .message("Post saved successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{postId}/save")
    public ResponseEntity<ApiResponse<Void>> unsavePost(
            @PathVariable Long postId) {

        savedPostService.unsavePost(postId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Post removed from saved posts")
                        .build()
        );
    }

    @GetMapping("/saved")
    public ResponseEntity<ApiResponse<Page<SavedPostResponse>>> getMySavedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<SavedPostResponse> response =
                savedPostService.getMySavedPosts(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<SavedPostResponse>>builder()
                        .success(true)
                        .message("Saved posts fetched successfully")
                        .data(response)
                        .build()
        );
    }
}