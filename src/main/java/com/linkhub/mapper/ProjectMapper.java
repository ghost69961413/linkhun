package com.linkhub.mapper;

import com.linkhub.dto.ProjectDto.ProjectResponse;
import com.linkhub.entity.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toResponse(Project project);

}