package com.linkhub.service;

import com.linkhub.dto.SearchDto.PostSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostSearchService {

    Page<PostSearchResponse> searchPosts(
            String query,
            Pageable pageable
    );
}