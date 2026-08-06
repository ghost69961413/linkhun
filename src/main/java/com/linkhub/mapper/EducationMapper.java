package com.linkhub.mapper;

import com.linkhub.dto.education.EducationResponse;
import com.linkhub.entity.Education;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    EducationResponse toResponse(Education education);

}