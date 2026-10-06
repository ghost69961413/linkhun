package com.linkhub.mapper;

import com.linkhub.dto.ProjectDto.ProjectResponse;
import com.linkhub.entity.Project;
import com.linkhub.entity.Technology;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "linkhubRepositoryId", source = "linkedRepository.id")
    @Mapping(target = "linkhubRepositoryPath", expression = "java(project.getLinkedRepository() == null ? null : \"/repositories/\" + project.getLinkedRepository().getOwner().getUsername() + \"/\" + project.getLinkedRepository().getName())")
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
