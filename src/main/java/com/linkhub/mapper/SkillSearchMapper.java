package com.linkhub.mapper;

import com.linkhub.dto.SearchDto.SkillSearchResponse;
import com.linkhub.entity.Skill;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillSearchMapper {

    SkillSearchResponse toResponse(Skill skill);
}