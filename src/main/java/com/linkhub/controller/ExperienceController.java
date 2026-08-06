package com.linkhub.controller;

import com.linkhub.dto.experience.ExperienceRequest;
import com.linkhub.dto.experience.ExperienceResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.ExperienceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experience")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExperienceResponse>> addExperience(
            @Valid @RequestBody ExperienceRequest request) {

        ExperienceResponse response = experienceService.addExperience(request);

        return ResponseEntity.ok(
                ApiResponse.<ExperienceResponse>builder()
                        .success(true)
                        .message("Experience added successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ExperienceResponse>>> getMyExperiences() {

        List<ExperienceResponse> response =
                experienceService.getMyExperiences();

        return ResponseEntity.ok(
                ApiResponse.<List<ExperienceResponse>>builder()
                        .success(true)
                        .message("Experiences fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<ExperienceResponse>> updateExperience(
            @PathVariable Long experienceId,
            @Valid @RequestBody ExperienceRequest request) {

        ExperienceResponse response =
                experienceService.updateExperience(experienceId, request);

        return ResponseEntity.ok(
                ApiResponse.<ExperienceResponse>builder()
                        .success(true)
                        .message("Experience updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(
            @PathVariable Long experienceId) {

        experienceService.deleteExperience(experienceId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Experience deleted successfully")
                        .build()
        );
    }
}