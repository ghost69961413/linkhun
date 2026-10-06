package com.linkhub.service.impl;

import com.linkhub.dto.SearchDto.UserSearchResponse;
import com.linkhub.mapper.UserSearchMapper;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.linkhub.enums.ProfileType;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final UserRepository userRepository;
    private final UserSearchMapper userSearchMapper;

    @Override
    public Page<UserSearchResponse> searchUsers(
            String query,
            ProfileType profileType,
            Pageable pageable) {

        String normalized = query == null ? "" : query.trim().toUpperCase(java.util.Locale.ROOT);
        try {
            ProfileType namedType = ProfileType.valueOf(normalized);
            if (profileType != null && profileType != namedType) return Page.empty(pageable);
            return userRepository.findByProfileProfileType(namedType, pageable).map(userSearchMapper::toResponse);
        } catch (IllegalArgumentException ignored) {
            // It is a normal text search, so continue matching names, headlines,
            // profile skills, and role-specific profile values.
        }

        return userRepository
                .searchProfiles(query == null ? "" : query.trim(), profileType, pageable)
                .map(userSearchMapper::toResponse);
    }
}
