package com.linkhub.controller;

import com.linkhub.dto.CommentDto.CommentRequest;
import com.linkhub.dto.CommentDto.CommentResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request) {

        CommentResponse response =
                commentService.addComment(postId, request);

        return ResponseEntity.ok(
                ApiResponse.<CommentResponse>builder()
                        .success(true)
                        .message("Comment added successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").ascending()
        );

        Page<CommentResponse> response =
                commentService.getComments(postId, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<CommentResponse>>builder()
                        .success(true)
                        .message("Comments fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request) {

        CommentResponse response =
                commentService.updateComment(commentId, request);

        return ResponseEntity.ok(
                ApiResponse.<CommentResponse>builder()
                        .success(true)
                        .message("Comment updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId) {

        commentService.deleteComment(commentId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Comment deleted successfully")
                        .build()
        );
    }
}