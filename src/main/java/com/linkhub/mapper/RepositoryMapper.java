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
                .ownerUsername(repository.getOwner() != null ? repository.getOwner().getUsername() : null)
                .initializeReadme(repository.getInitializeReadme())
                .license(repository.getLicense())
                .gitignoreTemplate(repository.getGitignoreTemplate())
                .defaultBranch(repository.getDefaultBranch())
                .filesCount(repository.getFilesCount() == null ? 0 : repository.getFilesCount())
                .stars(repository.getStars())
                .forks(repository.getForks())
                .createdAt(repository.getCreatedAt())
                .updatedAt(repository.getUpdatedAt())
                .build();
    }
}
