package com.linkhub.controller;

import com.linkhub.dto.SearchDto.PostSearchResponse;
import com.linkhub.dto.SearchDto.ProjectSearchResponse;
import com.linkhub.dto.SearchDto.SkillSearchResponse;
import com.linkhub.dto.SearchDto.UserSearchResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.PostSearchService;
import com.linkhub.service.ProjectSearchService;
import com.linkhub.service.SearchService;
import com.linkhub.service.SkillSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final PostSearchService postSearchService;
    private final ProjectSearchService projectSearchService;
    private final SkillSearchService skillSearchService;

    @GetMapping("/users")
    public ApiResponse<Page<UserSearchResponse>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("firstName").ascending()
        );

        return ApiResponse.<Page<UserSearchResponse>>builder()
                .success(true)
                .message("Users found successfully")
                .data(searchService.searchUsers(query, pageable))
                .build();
    }
    @GetMapping("/posts")
    public ApiResponse<Page<PostSearchResponse>> searchPosts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size
        );

        return ApiResponse.<Page<PostSearchResponse>>builder()
                .success(true)
                .message("Posts found successfully")
                .data(postSearchService.searchPosts(query, pageable))
                .build();
    }
    @GetMapping("/projects")
    public ApiResponse<Page<ProjectSearchResponse>> searchProjects(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size
        );

        return ApiResponse.<Page<ProjectSearchResponse>>builder()
                .success(true)
                .message("Projects found successfully")
                .data(projectSearchService.searchProjects(
                        query,
                        pageable
                ))
                .build();
    }
    @GetMapping("/skills")
    public ApiResponse<Page<SkillSearchResponse>> searchSkills(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size
        );

        return ApiResponse.<Page<SkillSearchResponse>>builder()
                .success(true)
                .message("Skills found successfully")
                .data(skillSearchService.searchSkills(
                        query,
                        pageable
                ))
                .build();
    }
}