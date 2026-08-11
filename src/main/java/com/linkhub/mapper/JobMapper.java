package com.linkhub.mapper;

import com.linkhub.dto.JobDto.JobApplicationResponse;
import com.linkhub.dto.JobDto.JobResponse;
import com.linkhub.entity.Job;
import com.linkhub.entity.JobApplication;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    // =========================
    // JOB → JOB RESPONSE
    // =========================

    public JobResponse toResponse(Job job) {

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .salary(job.getSalary())
                .jobType(job.getJobType())
                .skills(job.getSkills())
                .active(job.getActive())
                .postedById(
                        job.getPostedBy() != null
                                ? job.getPostedBy().getId()
                                : null
                )
                .postedByName(
                        job.getPostedBy() != null
                                ? job.getPostedBy().getFirstName()
                                : null
                )
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }


    // =========================
    // APPLICATION → RESPONSE
    // =========================

    public JobApplicationResponse toApplicationResponse(
            JobApplication application) {

        return JobApplicationResponse.builder()
                .id(application.getId())

                .jobId(
                        application.getJob() != null
                                ? application.getJob().getId()
                                : null
                )

                .jobTitle(
                        application.getJob() != null
                                ? application.getJob().getTitle()
                                : null
                )

                .applicantId(
                        application.getApplicant() != null
                                ? application.getApplicant().getId()
                                : null
                )

                .applicantName(
                        application.getApplicant() != null
                                ? application.getApplicant().getFirstName()
                                : null
                )

                .coverLetter(application.getCoverLetter())
                .resumeUrl(application.getResumeUrl())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}