package com.linkhub.mapper;

import com.linkhub.dto.ProjectDto.ProjectResponse;
import com.linkhub.entity.Project;
import com.linkhub.entity.Technology;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toResponse(Project project);

    default Set<String> map(Set<Technology> technologies) {

        if (technologies == null) {
            return Set.of();
        }

        return technologies.stream()
                .map(Technology::getName)
                .collect(Collectors.toSet());
    }
}