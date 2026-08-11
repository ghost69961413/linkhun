package com.linkhub.mapper;

import com.linkhub.dto.RepositoryDto.RepositoryResponse;
import com.linkhub.entity.Repository;
import org.springframework.stereotype.Component;

@Component
public class RepositoryMapper {

    public RepositoryResponse toResponse(Repository repository) {

        return RepositoryResponse.builder()
                .id(repository.getId())
                .name(repository.getName())
                .description(repository.getDescription())
                .repositoryUrl(repository.getRepositoryUrl())
                .language(repository.getLanguage())
                .visibility(repository.getVisibility())
                .ownerId(
                        repository.getOwner() != null
                                ? repository.getOwner().getId()
                                : null
                )
                .ownerName(
                        repository.getOwner() != null
                                ? repository.getOwner().getFirstName()
                                : null
                )
                .createdAt(repository.getCreatedAt())
                .updatedAt(repository.getUpdatedAt())
                .build();
    }
}