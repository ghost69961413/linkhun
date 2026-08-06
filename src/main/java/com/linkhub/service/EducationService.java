package com.linkhub.service;

import com.linkhub.dto.education.EducationRequest;
import com.linkhub.dto.education.EducationResponse;

import java.util.List;

public interface EducationService {

    EducationResponse addEducation(EducationRequest request);

    List<EducationResponse> getMyEducations();

    EducationResponse updateEducation(Long educationId,
                                      EducationRequest request);

    void deleteEducation(Long educationId);

}