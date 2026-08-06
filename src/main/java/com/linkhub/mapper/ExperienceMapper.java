package com.linkhub.mapper;

import com.linkhub.dto.experience.ExperienceResponse;
import com.linkhub.entity.Experience;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExperienceMapper {

    ExperienceResponse toResponse(Experience experience);

}