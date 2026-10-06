package com.linkhub.service;

import com.linkhub.dto.ProjectDto.ProjectRequest;
import com.linkhub.dto.ProjectDto.ProjectResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(
            ProjectRequest request,
            MultipartFile thumbnail,
            List<MultipartFile> screenshots
    );

    List<ProjectResponse> getMyProjects();
    List<ProjectResponse> getUserProjects(Long userId);

    ProjectResponse getProject(Long id);

    ProjectResponse updateProject(Long id, ProjectRequest request);

    void deleteProject(Long id);

}
