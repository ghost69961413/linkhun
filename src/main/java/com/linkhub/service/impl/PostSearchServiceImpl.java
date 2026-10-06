package com.linkhub.service.impl;

import com.linkhub.dto.SearchDto.PostSearchResponse;
import com.linkhub.mapper.PostSearchMapper;
import com.linkhub.repository.PostRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.entity.User;
import com.linkhub.enums.ConnectionStatus;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.service.PostSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class PostSearchServiceImpl implements PostSearchService {

    private final PostRepository postRepository;
    private final PostSearchMapper postSearchMapper;
    private final UserRepository userRepository;

    @Override
    public Page<PostSearchResponse> searchPosts(
            String query,
            Pageable pageable) {

        User viewer = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return postRepository
                .findVisiblePostsByContent(query, viewer, ConnectionStatus.ACCEPTED, pageable)
                .map(postSearchMapper::toResponse);
    }
}
