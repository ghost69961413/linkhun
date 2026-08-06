package com.linkhub.service;

import com.linkhub.dto.experience.ExperienceRequest;
import com.linkhub.dto.experience.ExperienceResponse;

import java.util.List;

public interface ExperienceService {

    ExperienceResponse addExperience(ExperienceRequest request);

    List<ExperienceResponse> getMyExperiences();

    ExperienceResponse updateExperience(Long experienceId,
                                        ExperienceRequest request);

    void deleteExperience(Long experienceId);

}