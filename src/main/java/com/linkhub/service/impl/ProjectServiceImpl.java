package com.linkhub.service.impl;

import com.linkhub.dto.ProjectDto.ProjectRequest;
import com.linkhub.dto.ProjectDto.ProjectResponse;
import com.linkhub.entity.Profile;
import com.linkhub.entity.Project;
import com.linkhub.entity.Technology;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.ProfileNotFoundException;
import com.linkhub.exception.ProjectNotFoundException;
import com.linkhub.mapper.ProjectMapper;
import com.linkhub.repository.ProfileRepository;
import com.linkhub.repository.ProjectRepository;
import com.linkhub.repository.TechnologyRepository;
import com.linkhub.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.linkhub.service.MediaService;
import com.linkhub.repository.RepositoryRepository;
import com.linkhub.entity.Repository;
import com.linkhub.enums.RepositoryVisibility;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TechnologyRepository technologyRepository;
    private final ProfileRepository profileRepository;
    private final ProjectMapper projectMapper;
    private final MediaService mediaService;
    private final RepositoryRepository repositoryRepository;


    private Profile getCurrentProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return profileRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new ProfileNotFoundException("Profile not found"));
    }

    @Override
    public ProjectResponse createProject(
            ProjectRequest request,
            MultipartFile thumbnail,
            List<MultipartFile> screenshots) {

        Profile profile = getCurrentProfile();

        Set<Technology> technologies = new HashSet<>();

        if (request.getTechnologies() != null) {

            for (String techName : request.getTechnologies()) {

                Technology technology = technologyRepository
                        .findByNameIgnoreCase(techName)
                        .orElseGet(() ->
                                technologyRepository.save(
                                        Technology.builder()
                                                .name(techName)
                                                .build()
                                ));

                technologies.add(technology);
            }
        }

        // Upload thumbnail to Cloudinary
        String thumbnailUrl = null;

        if (thumbnail != null && !thumbnail.isEmpty()) {

            thumbnailUrl = mediaService.uploadImage(
                    thumbnail,
                    "linkhub/projects"
            );
        }

        Repository linkedRepository = resolveRepository(request.getLinkhubRepositoryId(), profile.getUser().getId());
        String visibility = normalizeVisibility(request.getVisibility());
        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .githubUrl(request.getGithubUrl())
                .linkedRepository(linkedRepository)
                .liveDemoUrl(request.getLiveDemoUrl())
                .thumbnailUrl(thumbnailUrl)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .visibility(visibility)
                .featured(request.getFeatured())
                .profile(profile)
                .technologies(technologies)
                .features(cleanSet(request.getFeatures()))
                .teamMembers(cleanSet(request.getTeamMembers()))
                .build();

        if (screenshots != null) for (MultipartFile image : screenshots) {
            if (image != null && !image.isEmpty()) project.getScreenshots().add(mediaService.uploadImage(image, "linkhub/projects"));
        }

        return toResponse(projectRepository.save(project));
    }
    @Override
    public List<ProjectResponse> getMyProjects() {

        Profile profile = getCurrentProfile();

        return projectRepository.findByProfileId(profile.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ProjectResponse> getUserProjects(Long userId) {
        Profile owner = profileRepository.findByUserId(userId).orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
        Long viewerId = getCurrentProfile().getUser().getId();
        return projectRepository.findVisibleForUser(owner.getUser().getId(), viewerId).stream().map(this::toResponse).toList();
    }

    @Override
    public ProjectResponse getProject(Long id) {

        Long viewerId = getCurrentProfile().getUser().getId();
        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
         new ProjectNotFoundException("Project not found"));

        if (!isPublic(project) && !project.getProfile().getUser().getId().equals(viewerId))
            throw new ProjectNotFoundException("Project not found");

        return toResponse(project);
    }

    @Override
    public ProjectResponse updateProject(
            Long id,
            ProjectRequest request) {

        Profile currentProfile = getCurrentProfile();

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
         new ProjectNotFoundException("Project not found"));

        if (!project.getProfile()
                .getId()
                .equals(currentProfile.getId())) {

            throw new AccessDeniedException("You can only update your own project");
        }

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setGithubUrl(request.getGithubUrl());
        project.setLinkedRepository(resolveRepository(request.getLinkhubRepositoryId(), currentProfile.getUser().getId()));
        project.setLiveDemoUrl(request.getLiveDemoUrl());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(request.getStatus());
        if (request.getVisibility() != null) project.setVisibility(normalizeVisibility(request.getVisibility()));
        project.setFeatured(request.getFeatured());
        project.setFeatures(cleanSet(request.getFeatures()));
        project.setTeamMembers(cleanSet(request.getTeamMembers()));

        return toResponse(projectRepository.save(project));
    }

    private Repository resolveRepository(Long repositoryId, Long ownerId) {
        if (repositoryId == null) return null;
        Repository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new BadRequestException("LinkHub repository not found."));
        if (repository.getVisibility() == RepositoryVisibility.PRIVATE && !repository.getOwner().getId().equals(ownerId))
            throw new BadRequestException("You cannot link another user's private repository.");
        return repository;
    }
    @Override
    public void deleteProject(Long id) {

        Profile currentProfile = getCurrentProfile();

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
         new ProjectNotFoundException("Project not found"));

        if (!project.getProfile()
                .getId()
                .equals(currentProfile.getId())) {

            throw new AccessDeniedException("You can only delete your own project");
        }

        projectRepository.delete(project);
    }

    private ProjectResponse toResponse(Project project) {
        ProjectResponse response = projectMapper.toResponse(project);
        Profile owner = project.getProfile();
        var user = owner.getUser();
        response.setOwnerId(user.getId());
        response.setOwnerUsername(user.getUsername());
        response.setOwner(ProjectResponse.ProjectOwnerResponse.builder().id(user.getId()).userId(String.valueOf(user.getId()))
                .username(user.getUsername()).fullName((user.getFirstName() + " " + user.getLastName()).trim())
                .profilePicture(owner.getProfilePictureUrl() != null ? owner.getProfilePictureUrl() : user.getProfilePicture()).build());
        if (project.getLinkedRepository() != null
                && project.getLinkedRepository().getVisibility() == RepositoryVisibility.PRIVATE
                && !project.getLinkedRepository().getOwner().getId().equals(getCurrentProfile().getUser().getId())) {
            response.setLinkhubRepositoryId(null);
            response.setLinkhubRepositoryPath(null);
        }
        return response;
    }

    private static boolean isPublic(Project project) {
        return project.getVisibility() == null || project.getVisibility().isBlank() || "PUBLIC".equalsIgnoreCase(project.getVisibility());
    }

    private static String normalizeVisibility(String value) {
        if (value == null || value.isBlank()) return "PUBLIC";
        String normalized = value.trim().toUpperCase(java.util.Locale.ROOT);
        if (!normalized.equals("PUBLIC") && !normalized.equals("PRIVATE")) throw new BadRequestException("Project visibility must be PUBLIC or PRIVATE.");
        return normalized;
    }

    private static Set<String> cleanSet(Set<String> values) {
        if (values == null) return new HashSet<>();
        Set<String> clean = new HashSet<>();
        for (String value : values) if (value != null && !value.isBlank()) clean.add(value.trim());
        return clean;
    }
}
