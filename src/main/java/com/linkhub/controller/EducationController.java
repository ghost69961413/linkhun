package com.linkhub.controller;

import com.linkhub.dto.education.EducationRequest;
import com.linkhub.dto.education.EducationResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.EducationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    public ResponseEntity<ApiResponse<EducationResponse>> addEducation(
            @Valid @RequestBody EducationRequest request) {

        EducationResponse response = educationService.addEducation(request);

        return ResponseEntity.ok(
                ApiResponse.<EducationResponse>builder()
                        .success(true)
                        .message("Education added successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<EducationResponse>>> getMyEducations() {

        List<EducationResponse> response =
                educationService.getMyEducations();

        return ResponseEntity.ok(
                ApiResponse.<List<EducationResponse>>builder()
                        .success(true)
                        .message("Education fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{educationId}")
    public ResponseEntity<ApiResponse<EducationResponse>> updateEducation(
            @PathVariable Long educationId,
            @Valid @RequestBody EducationRequest request) {

        EducationResponse response =
                educationService.updateEducation(educationId, request);

        return ResponseEntity.ok(
                ApiResponse.<EducationResponse>builder()
                        .success(true)
                        .message("Education updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @PathVariable Long educationId) {

        educationService.deleteEducation(educationId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Education deleted successfully")
                        .build()
        );
    }
}