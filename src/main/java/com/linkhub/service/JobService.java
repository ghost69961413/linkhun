package com.linkhub.service;

import com.linkhub.dto.JobDto.JobApplicationRequest;
import com.linkhub.dto.JobDto.JobApplicationResponse;
import com.linkhub.dto.JobDto.JobRequest;
import com.linkhub.dto.JobDto.JobResponse;
import com.linkhub.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobService {

    // =========================
    // JOB
    // =========================

    JobResponse createJob(JobRequest request);

    Page<JobResponse> getAllJobs(Pageable pageable);

    Page<JobResponse> getMyJobs(Pageable pageable);

    JobResponse getJob(Long jobId);

    Page<JobResponse> searchJobs(
            String title,
            String location,
            Pageable pageable
    );

    JobResponse updateJob(
            Long jobId,
            JobRequest request
    );

    void deleteJob(Long jobId);


    // =========================
    // JOB APPLICATION
    // =========================

    JobApplicationResponse applyForJob(
            Long jobId,
            JobApplicationRequest request
    );

    Page<JobApplicationResponse> getMyApplications(
            Pageable pageable
    );

    Page<JobApplicationResponse> getJobApplications(
            Long jobId,
            Pageable pageable
    );

    JobApplicationResponse updateApplicationStatus(
            Long applicationId,
            ApplicationStatus status
    );
}