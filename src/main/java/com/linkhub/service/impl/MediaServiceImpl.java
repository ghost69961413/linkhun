package com.linkhub.service.impl;

import com.linkhub.exception.BadRequestException;
import com.linkhub.service.MediaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {
    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 25L * 1024 * 1024;

    private static final Map<String, String> IMAGE_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/gif", ".gif",
            "image/webp", ".webp"
    );
    private static final Map<String, String> VIDEO_TYPES = Map.of(
            "video/mp4", ".mp4",
            "video/webm", ".webm",
            "video/quicktime", ".mov"
    );

    @Value("${linkhub.upload-dir:uploads}")
    private String uploadDirectory;

    @Value("${linkhub.public-base-url:http://localhost:8080}")
    private String publicBaseUrl;

    @Override
    public Resource load(String storedUrl) {
        String marker = "/uploads/";
        int markerIndex = storedUrl == null ? -1 : storedUrl.indexOf(marker);
        if (markerIndex < 0) throw new BadRequestException("Media is not stored on this server");
        String relative = storedUrl.substring(markerIndex + marker.length());
        Path root = Path.of(uploadDirectory).toAbsolutePath().normalize();
        Path file = root.resolve(relative).normalize();
        if (!file.startsWith(root) || !Files.isRegularFile(file)) throw new BadRequestException("Media file was not found");
        try {
            return new UrlResource(file.toUri());
        } catch (IOException e) {
            throw new IllegalStateException("Could not read uploaded media", e);
        }
    }

    @Override
    public String uploadImage(MultipartFile file, String folder) {
        return store(file, IMAGE_TYPES, MAX_IMAGE_SIZE, "Image", folder);
    }

    @Override
    public String uploadVideo(MultipartFile file, String folder) {
        return store(file, VIDEO_TYPES, MAX_VIDEO_SIZE, "Video", folder);
    }

    private String store(MultipartFile file, Map<String, String> allowedTypes, long maxBytes, String label, String folder) {
        if (file == null || file.isEmpty()) throw new BadRequestException(label + " file is required");
        if (file.getSize() > maxBytes) throw new BadRequestException(label + " must not exceed " + (maxBytes / (1024 * 1024)) + " MB");
        String contentType = file.getContentType();
        String extension = contentType == null ? null : allowedTypes.get(contentType.toLowerCase());
        if (extension == null) throw new BadRequestException("Unsupported " + label.toLowerCase() + " format");

        String safeFolder = folder == null ? "posts" : folder.replaceAll("[^a-zA-Z0-9_-]", "_");
        String fileName = UUID.randomUUID() + extension;
        Path directory = Path.of(uploadDirectory).toAbsolutePath().normalize().resolve(safeFolder).normalize();
        Path destination = directory.resolve(fileName).normalize();
        if (!destination.startsWith(directory)) throw new BadRequestException("Invalid upload destination");

        try {
            Files.createDirectories(directory);
            file.transferTo(destination);
        } catch (IOException e) {
            throw new IllegalStateException("Could not store uploaded media", e);
        }

        return publicBaseUrl.replaceAll("/$", "") + "/uploads/" + safeFolder + "/" + fileName;
    }
}
