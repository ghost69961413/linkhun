package com.linkhub.service;

import com.linkhub.dto.SearchDto.SkillSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SkillSearchService {

    Page<SkillSearchResponse> searchSkills(
            String query,
            Pageable pageable
    );
}