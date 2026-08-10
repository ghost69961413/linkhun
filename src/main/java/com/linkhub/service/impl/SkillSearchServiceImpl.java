package com.linkhub.service.impl;

import com.linkhub.dto.SearchDto.SkillSearchResponse;
import com.linkhub.mapper.SkillSearchMapper;
import com.linkhub.repository.SkillRepository;
import com.linkhub.service.SkillSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillSearchServiceImpl implements SkillSearchService {

    private final SkillRepository skillRepository;
    private final SkillSearchMapper skillSearchMapper;

    @Override
    public Page<SkillSearchResponse> searchSkills(
            String query,
            Pageable pageable) {

        return skillRepository
                .findByNameContainingIgnoreCase(query, pageable)
                .map(skillSearchMapper::toResponse);
    }
}