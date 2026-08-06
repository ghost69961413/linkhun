package com.linkhub.service.impl;

import com.linkhub.dto.experience.ExperienceRequest;
import com.linkhub.dto.experience.ExperienceResponse;
import com.linkhub.entity.Experience;
import com.linkhub.entity.Profile;
import com.linkhub.exception.ProfileNotFoundException;
import com.linkhub.mapper.ExperienceMapper;
import com.linkhub.repository.ExperienceRepository;
import com.linkhub.repository.ProfileRepository;
import com.linkhub.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final ProfileRepository profileRepository;
    private final ExperienceMapper experienceMapper;

    private Profile getCurrentProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return profileRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new ProfileNotFoundException("Profile not found"));
    }

    @Override
    public ExperienceResponse addExperience(ExperienceRequest request) {

        Profile profile = getCurrentProfile();

        Experience experience = Experience.builder()
                .companyName(request.getCompanyName())
                .designation(request.getDesignation())
                .employmentType(request.getEmploymentType())
                .location(request.getLocation())
                .startYear(request.getStartYear())
                .endYear(request.getEndYear())
                .currentlyWorking(request.getCurrentlyWorking())
                .description(request.getDescription())
                .profile(profile)
                .build();

        return experienceMapper.toResponse(
                experienceRepository.save(experience)
        );
    }

    @Override
    public List<ExperienceResponse> getMyExperiences() {

        Profile profile = getCurrentProfile();

        return experienceRepository.findByProfileId(profile.getId())
                .stream()
                .map(experienceMapper::toResponse)
                .toList();
    }

    @Override
    public ExperienceResponse updateExperience(Long experienceId,
                                               ExperienceRequest request) {

        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() ->
                        new RuntimeException("Experience not found"));

        experience.setCompanyName(request.getCompanyName());
        experience.setDesignation(request.getDesignation());
        experience.setEmploymentType(request.getEmploymentType());
        experience.setLocation(request.getLocation());
        experience.setStartYear(request.getStartYear());
        experience.setEndYear(request.getEndYear());
        experience.setCurrentlyWorking(request.getCurrentlyWorking());
        experience.setDescription(request.getDescription());

        return experienceMapper.toResponse(
                experienceRepository.save(experience)
        );
    }

    @Override
    public void deleteExperience(Long experienceId) {

        experienceRepository.deleteById(experienceId);
    }
}