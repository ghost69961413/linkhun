package com.linkhub.controller;

import com.linkhub.dto.JobDto.JobApplicationRequest;
import com.linkhub.dto.JobDto.JobApplicationResponse;
import com.linkhub.dto.JobDto.JobRequest;
import com.linkhub.dto.JobDto.JobResponse;
import com.linkhub.enums.ApplicationStatus;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;


    // =====================================================
    // CREATE JOB
    // =====================================================

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @Valid @RequestBody JobRequest request) {

        JobResponse response =
                jobService.createJob(request);

        return ResponseEntity.ok(
                ApiResponse.<JobResponse>builder()
                        .success(true)
                        .message("Job created successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // GET ALL JOBS
    // =====================================================

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<JobResponse> response =
                jobService.getAllJobs(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<JobResponse>>builder()
                        .success(true)
                        .message("Jobs fetched successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // GET MY JOBS
    // =====================================================

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getMyJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<JobResponse> response =
                jobService.getMyJobs(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<JobResponse>>builder()
                        .success(true)
                        .message("Your jobs fetched successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // SEARCH JOBS
    // =====================================================

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<JobResponse> response =
                jobService.searchJobs(
                        title,
                        location,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.<Page<JobResponse>>builder()
                        .success(true)
                        .message("Jobs searched successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // GET JOB BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJob(
            @PathVariable Long id) {

        JobResponse response =
                jobService.getJob(id);

        return ResponseEntity.ok(
                ApiResponse.<JobResponse>builder()
                        .success(true)
                        .message("Job fetched successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // UPDATE JOB
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request) {

        JobResponse response =
                jobService.updateJob(
                        id,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.<JobResponse>builder()
                        .success(true)
                        .message("Job updated successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // DELETE / DEACTIVATE JOB
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Long id) {

        jobService.deleteJob(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Job deleted successfully")
                        .build()
        );
    }


    // =====================================================
    // APPLY FOR JOB
    // =====================================================

    @PostMapping("/{id}/apply")
    public ResponseEntity<ApiResponse<JobApplicationResponse>>
    applyForJob(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationRequest request) {

        JobApplicationResponse response =
                jobService.applyForJob(
                        id,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.<JobApplicationResponse>builder()
                        .success(true)
                        .message("Job application submitted successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // MY APPLICATIONS
    // =====================================================

    @GetMapping("/applications/me")
    public ResponseEntity<ApiResponse<Page<JobApplicationResponse>>>
    getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<JobApplicationResponse> response =
                jobService.getMyApplications(pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<JobApplicationResponse>>builder()
                        .success(true)
                        .message("Your job applications fetched successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // JOB OWNER → GET APPLICATIONS
    // =====================================================

    @GetMapping("/{id}/applications")
    public ResponseEntity<ApiResponse<Page<JobApplicationResponse>>>
    getJobApplications(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<JobApplicationResponse> response =
                jobService.getJobApplications(
                        id,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.<Page<JobApplicationResponse>>builder()
                        .success(true)
                        .message("Job applications fetched successfully")
                        .data(response)
                        .build()
        );
    }


    // =====================================================
    // ACCEPT / REJECT APPLICATION
    // =====================================================

    @PatchMapping("/applications/{applicationId}/status")
    public ResponseEntity<ApiResponse<JobApplicationResponse>>
    updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status) {

        JobApplicationResponse response =
                jobService.updateApplicationStatus(
                        applicationId,
                        status
                );

        return ResponseEntity.ok(
                ApiResponse.<JobApplicationResponse>builder()
                        .success(true)
                        .message("Application status updated successfully")
                        .data(response)
                        .build()
        );
    }
}