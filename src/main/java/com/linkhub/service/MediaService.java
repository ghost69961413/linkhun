package com.linkhub.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface MediaService {

    String uploadImage(
            MultipartFile file,
            String folder
    );

    String uploadVideo(
            MultipartFile file,
            String folder
    );

    Resource load(String storedUrl);
}
