package com.linkhub.service.impl;

import com.linkhub.dto.education.EducationRequest;
import com.linkhub.dto.education.EducationResponse;
import com.linkhub.entity.Education;
import com.linkhub.entity.Profile;
import com.linkhub.exception.ProfileNotFoundException;
import com.linkhub.mapper.EducationMapper;
import com.linkhub.repository.EducationRepository;
import com.linkhub.repository.ProfileRepository;
import com.linkhub.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final ProfileRepository profileRepository;
    private final EducationMapper educationMapper;

    private Profile getCurrentProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return profileRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new ProfileNotFoundException("Profile not found"));
    }

    @Override
    public EducationResponse addEducation(EducationRequest request) {

        Profile profile = getCurrentProfile();

        Education education = Education.builder()
                .collegeName(request.getCollegeName())
                .degree(request.getDegree())
                .fieldOfStudy(request.getFieldOfStudy())
                .startYear(request.getStartYear())
                .endYear(request.getEndYear())
                .grade(request.getGrade())
                .description(request.getDescription())
                .profile(profile)
                .build();

        return educationMapper.toResponse(
                educationRepository.save(education)
        );
    }

    @Override
    public List<EducationResponse> getMyEducations() {

        Profile profile = getCurrentProfile();

        return educationRepository.findByProfileId(profile.getId())
                .stream()
                .map(educationMapper::toResponse)
                .toList();
    }

    @Override
    public EducationResponse updateEducation(Long educationId,
                                             EducationRequest request) {

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() ->
                        new RuntimeException("Education not found"));

        education.setCollegeName(request.getCollegeName());
        education.setDegree(request.getDegree());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartYear(request.getStartYear());
        education.setEndYear(request.getEndYear());
        education.setGrade(request.getGrade());
        education.setDescription(request.getDescription());

        return educationMapper.toResponse(
                educationRepository.save(education)
        );
    }

    @Override
    public void deleteEducation(Long educationId) {

        educationRepository.deleteById(educationId);
    }
}