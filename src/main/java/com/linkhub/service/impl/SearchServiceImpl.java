package com.linkhub.service.impl;

import com.linkhub.dto.SearchDto.UserSearchResponse;
import com.linkhub.mapper.UserSearchMapper;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final UserRepository userRepository;
    private final UserSearchMapper userSearchMapper;

    @Override
    public Page<UserSearchResponse> searchUsers(
            String query,
            Pageable pageable) {

        return userRepository
                .findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        query,
                        query,
                        query,
                        pageable
                )
                .map(userSearchMapper::toResponse);
    }
}