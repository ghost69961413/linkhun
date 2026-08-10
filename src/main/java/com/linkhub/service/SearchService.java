package com.linkhub.service;

import com.linkhub.dto.SearchDto.UserSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {

    Page<UserSearchResponse> searchUsers(
            String query,
            Pageable pageable
    );
}