package com.linkhub.mapper;

import com.linkhub.dto.SearchDto.ProjectSearchResponse;
import com.linkhub.entity.Project;
import com.linkhub.entity.Technology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectSearchMapper {

    @Mapping(source = "profile.id", target = "profileId")
    ProjectSearchResponse toResponse(Project project);

    default Set<String> map(Set<Technology> technologies) {

        if (technologies == null) {
            return Set.of();
        }

        return technologies.stream()
                .map(Technology::getName)
                .collect(Collectors.toSet());
    }
}