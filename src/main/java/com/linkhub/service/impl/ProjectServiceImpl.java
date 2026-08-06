package com.linkhub.service.impl;

import com.linkhub.dto.ProjectDto.ProjectRequest;
import com.linkhub.dto.ProjectDto.ProjectResponse;
import com.linkhub.entity.Profile;
import com.linkhub.entity.Project;
import com.linkhub.entity.Technology;
import com.linkhub.exception.ProfileNotFoundException;
import com.linkhub.mapper.ProjectMapper;
import com.linkhub.repository.ProfileRepository;
import com.linkhub.repository.ProjectRepository;
import com.linkhub.repository.TechnologyRepository;
import com.linkhub.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TechnologyRepository technologyRepository;
    private final ProfileRepository profileRepository;
    private final ProjectMapper projectMapper;

    private Profile getCurrentProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return profileRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new ProfileNotFoundException("Profile not found"));
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request) {

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

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .githubUrl(request.getGithubUrl())
                .liveDemoUrl(request.getLiveDemoUrl())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .visibility(request.getVisibility())
                .featured(request.getFeatured())
                .profile(profile)
                .technologies(technologies)
                .build();

        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    public List<ProjectResponse> getMyProjects() {

        Profile profile = getCurrentProfile();

        return projectRepository.findByProfileId(profile.getId())
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Override
    public ProjectResponse getProject(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));

        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id,
                                         ProjectRequest request) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setGithubUrl(request.getGithubUrl());
        project.setLiveDemoUrl(request.getLiveDemoUrl());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(request.getStatus());
        project.setVisibility(request.getVisibility());
        project.setFeatured(request.getFeatured());

        return projectMapper.toResponse(
                projectRepository.save(project)
        );
    }

    @Override
    public void deleteProject(Long id) {

        projectRepository.deleteById(id);

    }
}