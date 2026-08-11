package com.linkhub.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.linkhub.exception.BadRequestException;
import com.linkhub.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final Cloudinary cloudinary;

    private static final long MAX_IMAGE_SIZE =
            5 * 1024 * 1024; // 5 MB

    private static final long MAX_VIDEO_SIZE =
            50 * 1024 * 1024; // 50 MB


    // =========================
    // IMAGE UPLOAD
    // =========================

    @Override
    public String uploadImage(
            MultipartFile file,
            String folder) {

        validateImage(file);

        try {

            Map<?, ?> result =
                    cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "resource_type", "image",
                                    "folder", folder
                            )
                    );

            return result
                    .get("secure_url")
                    .toString();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Image upload failed",
                    e
            );
        }
    }


    // =========================
    // VIDEO UPLOAD
    // =========================

    @Override
    public String uploadVideo(
            MultipartFile file,
            String folder) {

        validateVideo(file);

        try {

            Map<?, ?> result =
                    cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "resource_type", "video",
                                    "folder", folder
                            )
                    );

            return result
                    .get("secure_url")
                    .toString();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Video upload failed",
                    e
            );
        }
    }


    // =========================
    // IMAGE VALIDATION
    // =========================

    private void validateImage(
            MultipartFile file) {

        if (file == null || file.isEmpty()) {

            throw new BadRequestException(
                    "Image is required"
            );
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {

            throw new BadRequestException(
                    "Image size must not exceed 5 MB"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null ||
                !contentType.startsWith("image/")) {

            throw new BadRequestException(
                    "Only image files are allowed"
            );
        }
    }


    // =========================
    // VIDEO VALIDATION
    // =========================

    private void validateVideo(
            MultipartFile file) {

        if (file == null || file.isEmpty()) {

            throw new BadRequestException(
                    "Video is required"
            );
        }

        if (file.getSize() > MAX_VIDEO_SIZE) {

            throw new BadRequestException(
                    "Video size must not exceed 50 MB"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null ||
                !contentType.startsWith("video/")) {

            throw new BadRequestException(
                    "Only video files are allowed"
            );
        }
    }
}