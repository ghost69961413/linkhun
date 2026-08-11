package com.linkhub.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    String uploadImage(
            MultipartFile file,
            String folder
    );

    String uploadVideo(
            MultipartFile file,
            String folder
    );
}