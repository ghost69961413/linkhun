package com.linkhub.service;

import com.linkhub.dto.SearchDto.UserSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.linkhub.enums.ProfileType;

public interface SearchService {

    Page<UserSearchResponse> searchUsers(
            String query,
            ProfileType profileType,
            Pageable pageable
    );
}
