package com.linkhub.service.impl;

import com.linkhub.dto.skill.SkillRequest;
import com.linkhub.dto.skill.SkillResponse;
import com.linkhub.entity.Profile;
import com.linkhub.entity.Skill;
import com.linkhub.exception.DuplicateResourceException;
import com.linkhub.exception.ProfileNotFoundException;
import com.linkhub.exception.SkillNotFoundException;
import com.linkhub.mapper.SkillMapper;
import com.linkhub.repository.ProfileRepository;
import com.linkhub.repository.SkillRepository;
import com.linkhub.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final ProfileRepository profileRepository;
    private final SkillMapper skillMapper;

    @Override
    public SkillResponse createSkill(SkillRequest request) {

        if (skillRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Skill already exists");
        }

        Skill skill = Skill.builder()
                .name(request.getName())
                .build();

        return skillMapper.toResponse(skillRepository.save(skill));
    }

    @Override
    public List<SkillResponse> getAllSkills() {

        return skillRepository.findAll()
                .stream()
                .map(skillMapper::toResponse)
                .toList();
    }

    @Override
    public void addSkillToProfile(Long skillId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Profile profile = profileRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new ProfileNotFoundException("Profile not found"));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new SkillNotFoundException("Skill not found"));

        profile.getSkills().add(skill);

        profileRepository.save(profile);
    }

    @Override
    public void removeSkillFromProfile(Long skillId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Profile profile = profileRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new ProfileNotFoundException("Profile not found"));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new SkillNotFoundException("Skill not found"));

        profile.getSkills().remove(skill);

        profileRepository.save(profile);
    }
}