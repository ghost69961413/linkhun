package com.linkhub.service;

import com.linkhub.dto.SearchDto.ProjectSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectSearchService {

    Page<ProjectSearchResponse> searchProjects(
            String query,
            Pageable pageable
    );
}