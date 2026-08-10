package com.linkhub.service.impl;

import com.linkhub.dto.SearchDto.ProjectSearchResponse;
import com.linkhub.mapper.ProjectSearchMapper;
import com.linkhub.repository.ProjectRepository;
import com.linkhub.service.ProjectSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectSearchServiceImpl implements ProjectSearchService {

    private final ProjectRepository projectRepository;
    private final ProjectSearchMapper projectSearchMapper;

    @Override
    public Page<ProjectSearchResponse> searchProjects(
            String query,
            Pageable pageable) {

        return projectRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        query,
                        query,
                        pageable
                )
                .map(projectSearchMapper::toResponse);
    }
}