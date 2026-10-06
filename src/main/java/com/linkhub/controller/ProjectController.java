package com.linkhub.controller;

import com.linkhub.dto.ProjectDto.ProjectRequest;
import com.linkhub.dto.ProjectDto.ProjectResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @ModelAttribute ProjectRequest request,
            @RequestParam(value = "thumbnail", required = false)
            MultipartFile thumbnail,
            @RequestParam(value = "screenshots", required = false) List<MultipartFile> screenshots) {

        ProjectResponse response =
                projectService.createProject(request, thumbnail, screenshots);

        return ResponseEntity.ok(
                ApiResponse.<ProjectResponse>builder()
                        .success(true)
                        .message("Project created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getMyProjects() {

        List<ProjectResponse> response =
                projectService.getMyProjects();

        return ResponseEntity.ok(
                ApiResponse.<List<ProjectResponse>>builder()
                        .success(true)
                        .message("Projects fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getUserProjects(@PathVariable Long userId) {
        List<ProjectResponse> response = projectService.getUserProjects(userId);
        return ResponseEntity.ok(ApiResponse.<List<ProjectResponse>>builder().success(true)
                .message("Visible projects fetched successfully").data(response).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @PathVariable Long id) {

        ProjectResponse response = projectService.getProject(id);

        return ResponseEntity.ok(
                ApiResponse.<ProjectResponse>builder()
                        .success(true)
                        .message("Project fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {

        ProjectResponse response =
                projectService.updateProject(id, request);

        return ResponseEntity.ok(
                ApiResponse.<ProjectResponse>builder()
                        .success(true)
                        .message("Project updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long id) {

        projectService.deleteProject(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Project deleted successfully")
                        .build()
        );
    }
}
