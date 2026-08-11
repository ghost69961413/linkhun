package com.linkhub.controller;

import com.linkhub.response.ApiResponse;
import com.linkhub.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("file") MultipartFile file) {

        String imageUrl = mediaService.uploadImage(
                file,
                "linkhub/posts"
        );
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Image uploaded successfully")
                        .data(imageUrl)
                        .build()
        );
    }

    @PostMapping("/video")
    public ResponseEntity<ApiResponse<String>> uploadVideo(
            @RequestParam("file") MultipartFile file) {

        String videoUrl = mediaService.uploadVideo(
                file,
                "linkhub/posts"
        );

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Video uploaded successfully")
                        .data(videoUrl)
                        .build()
        );
    }
}