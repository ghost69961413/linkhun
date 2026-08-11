package com.linkhub.service;

import com.linkhub.dto.RepositoryDto.RepositoryRequest;
import com.linkhub.dto.RepositoryDto.RepositoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RepositoryService {

    RepositoryResponse createRepository(RepositoryRequest request);

    Page<RepositoryResponse> getMyRepositories(Pageable pageable);

    Page<RepositoryResponse> getPublicRepositories(Pageable pageable);

    RepositoryResponse getRepository(Long id);

    Page<RepositoryResponse> searchRepositories(
            String name,
            Pageable pageable
    );

    RepositoryResponse updateRepository(
            Long id,
            RepositoryRequest request
    );

    void deleteRepository(Long id);
}