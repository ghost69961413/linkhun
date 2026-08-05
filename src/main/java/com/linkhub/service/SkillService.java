package com.linkhub.service;

import com.linkhub.dto.skill.SkillRequest;
import com.linkhub.dto.skill.SkillResponse;

import java.util.List;

public interface SkillService {

    SkillResponse createSkill(SkillRequest request);

    List<SkillResponse> getAllSkills();

    void addSkillToProfile(Long skillId);

    void removeSkillFromProfile(Long skillId);

}