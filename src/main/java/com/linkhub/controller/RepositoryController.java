package com.linkhub.controller;

import com.linkhub.dto.RepositoryDto.RepositoryRequest;
import com.linkhub.dto.RepositoryDto.RepositoryResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.RepositoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repositories")
@RequiredArgsConstructor
public class RepositoryController {

    private final RepositoryService repositoryService;


    // =====================================================
    // CREATE REPOSITORY
    // =====================================================

    @PostMapping
    public ResponseEntity<ApiResponse<RepositoryResponse>> createRepository(
            @Valid @RequestBody RepositoryRequest request) {

        RepositoryResponse response =
                repositoryService.createRepository(request);

        return ResponseEntity.ok(
                ApiResponse.<RepositoryResponse>builder()
                        .success(true)
                        .message("Repository created successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // GET PUBLIC REPOSITORIES
    // =====================================================

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RepositoryResponse>>>
    getPublicRepositories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<RepositoryResponse> response =
                repositoryService.getPublicRepositories(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<RepositoryResponse>>builder()
                        .success(true)
                        .message("Public repositories fetched successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // GET MY REPOSITORIES
    // =====================================================

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<RepositoryResponse>>>
    getMyRepositories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<RepositoryResponse> response =
                repositoryService.getMyRepositories(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<RepositoryResponse>>builder()
                        .success(true)
                        .message("Your repositories fetched successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // SEARCH REPOSITORIES
    // =====================================================

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<RepositoryResponse>>>
    searchRepositories(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<RepositoryResponse> response =
                repositoryService.searchRepositories(
                        name,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.<Page<RepositoryResponse>>builder()
                        .success(true)
                        .message("Repositories searched successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // GET REPOSITORY BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RepositoryResponse>>
    getRepository(@PathVariable Long id) {

        RepositoryResponse response =
                repositoryService.getRepository(id);

        return ResponseEntity.ok(
                ApiResponse.<RepositoryResponse>builder()
                        .success(true)
                        .message("Repository fetched successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // UPDATE REPOSITORY
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RepositoryResponse>>
    updateRepository(
            @PathVariable Long id,
            @Valid @RequestBody RepositoryRequest request) {

        RepositoryResponse response =
                repositoryService.updateRepository(
                        id,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.<RepositoryResponse>builder()
                        .success(true)
                        .message("Repository updated successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // DELETE REPOSITORY
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>>
    deleteRepository(@PathVariable Long id) {

        repositoryService.deleteRepository(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Repository deleted successfully")
                        .build()
        );
    }
}