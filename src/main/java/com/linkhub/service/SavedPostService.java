package com.linkhub.service;

import com.linkhub.dto.SavedPostDto.SavedPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SavedPostService {

    SavedPostResponse savePost(Long postId);

    void unsavePost(Long postId);

    Page<SavedPostResponse> getMySavedPosts(Pageable pageable);
}