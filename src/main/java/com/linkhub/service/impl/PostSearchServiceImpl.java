package com.linkhub.service.impl;

import com.linkhub.dto.SearchDto.PostSearchResponse;
import com.linkhub.mapper.PostSearchMapper;
import com.linkhub.repository.PostRepository;
import com.linkhub.service.PostSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostSearchServiceImpl implements PostSearchService {

    private final PostRepository postRepository;
    private final PostSearchMapper postSearchMapper;

    @Override
    public Page<PostSearchResponse> searchPosts(
            String query,
            Pageable pageable) {

        return postRepository
                .findByContentContainingIgnoreCase(query, pageable)
                .map(postSearchMapper::toResponse);
    }
}