package com.linkhub.service.impl;

import com.linkhub.dto.RepositoryDto.RepositoryRequest;
import com.linkhub.dto.RepositoryDto.RepositoryResponse;
import com.linkhub.entity.Repository;
import com.linkhub.entity.User;
import com.linkhub.enums.RepositoryVisibility;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.RepositoryMapper;
import com.linkhub.repository.RepositoryRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepositoryServiceImpl implements RepositoryService {

    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final RepositoryMapper repositoryMapper;


    // =====================================================
    // CURRENT USER
    // =====================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));
    }


    // =====================================================
    // GET REPOSITORY
    // =====================================================

    private Repository findRepository(Long id) {

        return repositoryRepository.findById(id)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Repository not found"
                        ));
    }


    // =====================================================
    // CREATE
    // =====================================================

    @Override
    @Transactional
    public RepositoryResponse createRepository(
            RepositoryRequest request) {

        User currentUser = getCurrentUser();

        Repository repository = Repository.builder()
                .name(request.getName())
                .description(request.getDescription())
                .repositoryUrl(request.getRepositoryUrl())
                .language(request.getLanguage())
                .visibility(
                        request.getVisibility() != null
                                ? request.getVisibility()
                                : RepositoryVisibility.PUBLIC
                )
                .owner(currentUser)
                .build();

        Repository savedRepository =
                repositoryRepository.save(repository);

        return repositoryMapper.toResponse(savedRepository);
    }


    // =====================================================
    // MY REPOSITORIES
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<RepositoryResponse> getMyRepositories(
            Pageable pageable) {

        User currentUser = getCurrentUser();

        return repositoryRepository
                .findByOwner(currentUser, pageable)
                .map(repositoryMapper::toResponse);
    }


    // =====================================================
    // PUBLIC REPOSITORIES
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<RepositoryResponse> getPublicRepositories(
            Pageable pageable) {

        return repositoryRepository
                .findByVisibility(
                        RepositoryVisibility.PUBLIC,
                        pageable
                )
                .map(repositoryMapper::toResponse);
    }


    // =====================================================
    // GET BY ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public RepositoryResponse getRepository(Long id) {

        Repository repository = findRepository(id);

        User currentUser = getCurrentUser();

        // Private repository can only be viewed by owner
        if (repository.getVisibility()
                == RepositoryVisibility.PRIVATE
                && !repository.getOwner()
                .getId()
                .equals(currentUser.getId())) {

            throw new BadRequestException(
                    "You cannot access this private repository"
            );
        }

        return repositoryMapper.toResponse(repository);
    }


    // =====================================================
    // SEARCH
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<RepositoryResponse> searchRepositories(
            String name,
            Pageable pageable) {

        if (name == null || name.isBlank()) {

            return repositoryRepository
                    .findByVisibility(
                            RepositoryVisibility.PUBLIC,
                            pageable
                    )
                    .map(repositoryMapper::toResponse);
        }

        return repositoryRepository
                .findByNameContainingIgnoreCase(
                        name.trim(),
                        pageable
                )
                .map(repositoryMapper::toResponse);
    }


    // =====================================================
    // UPDATE
    // =====================================================

    @Override
    @Transactional
    public RepositoryResponse updateRepository(
            Long id,
            RepositoryRequest request) {

        User currentUser = getCurrentUser();

        Repository repository = findRepository(id);

        if (!repository.getOwner()
                .getId()
                .equals(currentUser.getId())) {

            throw new BadRequestException(
                    "You can only update your own repository"
            );
        }

        repository.setName(request.getName());
        repository.setDescription(request.getDescription());
        repository.setRepositoryUrl(request.getRepositoryUrl());
        repository.setLanguage(request.getLanguage());

        if (request.getVisibility() != null) {
            repository.setVisibility(
                    request.getVisibility()
            );
        }

        return repositoryMapper.toResponse(
                repositoryRepository.save(repository)
        );
    }


    // =====================================================
    // DELETE
    // =====================================================

    @Override
    @Transactional
    public void deleteRepository(Long id) {

        User currentUser = getCurrentUser();

        Repository repository = findRepository(id);

        if (!repository.getOwner()
                .getId()
                .equals(currentUser.getId())) {

            throw new BadRequestException(
                    "You can only delete your own repository"
            );
        }

        repositoryRepository.delete(repository);
    }
}