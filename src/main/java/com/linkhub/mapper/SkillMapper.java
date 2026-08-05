package com.linkhub.mapper;

import com.linkhub.dto.skill.SkillResponse;
import com.linkhub.entity.Skill;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    SkillResponse toResponse(Skill skill);

}