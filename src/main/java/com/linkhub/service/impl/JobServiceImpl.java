package com.linkhub.service.impl;

import com.linkhub.dto.JobDto.JobApplicationRequest;
import com.linkhub.dto.JobDto.JobApplicationResponse;
import com.linkhub.dto.JobDto.JobRequest;
import com.linkhub.dto.JobDto.JobResponse;
import com.linkhub.entity.Job;
import com.linkhub.entity.JobApplication;
import com.linkhub.entity.User;
import com.linkhub.enums.ApplicationStatus;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.JobNotFoundException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.JobMapper;
import com.linkhub.repository.JobApplicationRepository;
import com.linkhub.repository.JobRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final JobMapper jobMapper;


    // =====================================================
    // CURRENT USER
    // =====================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));
    }


    // =====================================================
    // FIND JOB
    // =====================================================

    private Job findJob(Long jobId) {

        return jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job not found"
                        ));
    }


    // =====================================================
    // CREATE JOB
    // =====================================================

    @Override
    @Transactional
    public JobResponse createJob(JobRequest request) {

        User currentUser = getCurrentUser();

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .companyName(request.getCompanyName())
                .location(request.getLocation())
                .salary(request.getSalary())
                .jobType(request.getJobType())
                .skills(request.getSkills())
                .active(true)
                .postedBy(currentUser)
                .build();

        Job savedJob = jobRepository.save(job);

        return jobMapper.toResponse(savedJob);
    }


    // =====================================================
    // GET ALL ACTIVE JOBS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> getAllJobs(Pageable pageable) {

        return jobRepository
                .findByActiveTrue(pageable)
                .map(jobMapper::toResponse);
    }


    // =====================================================
    // GET MY JOBS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> getMyJobs(Pageable pageable) {

        User currentUser = getCurrentUser();

        return jobRepository
                .findByPostedByAndActiveTrue(
                        currentUser,
                        pageable
                )
                .map(jobMapper::toResponse);
    }


    // =====================================================
    // GET JOB BY ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJob(Long jobId) {

        Job job = findJob(jobId);

        if (!Boolean.TRUE.equals(job.getActive())) {

            throw new JobNotFoundException(
                    "Job is no longer available"
            );
        }

        return jobMapper.toResponse(job);
    }


    // =====================================================
    // SEARCH JOBS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> searchJobs(
            String title,
            String location,
            Pageable pageable) {

        boolean hasTitle =
                title != null &&
                        !title.isBlank();

        boolean hasLocation =
                location != null &&
                        !location.isBlank();


        if (hasTitle) {

            return jobRepository
                    .findByTitleContainingIgnoreCaseAndActiveTrue(
                            title.trim(),
                            pageable
                    )
                    .map(jobMapper::toResponse);
        }


        if (hasLocation) {

            return jobRepository
                    .findByLocationContainingIgnoreCaseAndActiveTrue(
                            location.trim(),
                            pageable
                    )
                    .map(jobMapper::toResponse);
        }


        return jobRepository
                .findByActiveTrue(pageable)
                .map(jobMapper::toResponse);
    }


    // =====================================================
    // UPDATE JOB
    // =====================================================

    @Override
    @Transactional
    public JobResponse updateJob(
            Long jobId,
            JobRequest request) {

        User currentUser = getCurrentUser();

        Job job = findJob(jobId);

        if (!job.getPostedBy()
                .getId()
                .equals(currentUser.getId())) {

            throw new BadRequestException(
                    "You can only update your own job"
            );
        }

        if (!Boolean.TRUE.equals(job.getActive())) {

            throw new JobNotFoundException(
                    "Job is no longer available"
            );
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCompanyName(request.getCompanyName());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setJobType(request.getJobType());
        job.setSkills(request.getSkills());

        return jobMapper.toResponse(
                jobRepository.save(job)
        );
    }


    // =====================================================
    // DELETE / DEACTIVATE JOB
    // =====================================================

    @Override
    @Transactional
    public void deleteJob(Long jobId) {

        User currentUser = getCurrentUser();

        Job job = findJob(jobId);

        if (!job.getPostedBy()
                .getId()
                .equals(currentUser.getId())) {

            throw new BadRequestException(
                    "You can only delete your own job"
            );
        }

        job.setActive(false);

        jobRepository.save(job);
    }


    // =====================================================
    // APPLY FOR JOB
    // =====================================================

    @Override
    @Transactional
    public JobApplicationResponse applyForJob(
            Long jobId,
            JobApplicationRequest request) {

        User currentUser = getCurrentUser();

        Job job = findJob(jobId);

        if (!Boolean.TRUE.equals(job.getActive())) {

            throw new JobNotFoundException(
                    "Job is no longer available"
            );
        }

        // Job owner cannot apply to own job
        if (job.getPostedBy()
                .getId()
                .equals(currentUser.getId())) {

            throw new BadRequestException(
                    "You cannot apply to your own job"
            );
        }

        // Prevent duplicate application
        if (jobApplicationRepository
                .existsByJobAndApplicant(
                        job,
                        currentUser
                )) {

            throw new BadRequestException(
                    "You have already applied for this job"
            );
        }

        JobApplication application =
                JobApplication.builder()
                        .job(job)
                        .applicant(currentUser)
                        .coverLetter(
                                request.getCoverLetter()
                        )
                        .resumeUrl(
                                request.getResumeUrl()
                        )
                        .status(
                                ApplicationStatus.PENDING
                        )
                        .build();

        JobApplication savedApplication =
                jobApplicationRepository.save(
                        application
                );

        return jobMapper.toApplicationResponse(
                savedApplication
        );
    }


    // =====================================================
    // MY APPLICATIONS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getMyApplications(
            Pageable pageable) {

        User currentUser = getCurrentUser();

        return jobApplicationRepository
                .findByApplicant(
                        currentUser,
                        pageable
                )
                .map(jobMapper::toApplicationResponse);
    }


    // =====================================================
    // JOB OWNER → GET APPLICATIONS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getJobApplications(
            Long jobId,
            Pageable pageable) {

        User currentUser = getCurrentUser();

        Job job = findJob(jobId);

        if (!job.getPostedBy()
                .getId()
                .equals(currentUser.getId())) {

            throw new BadRequestException(
                    "You can only view applications "
                            + "for your own job"
            );
        }

        return jobApplicationRepository
                .findByJob(
                        job,
                        pageable
                )
                .map(jobMapper::toApplicationResponse);
    }


    // =====================================================
    // ACCEPT / REJECT APPLICATION
    // =====================================================

    @Override
    @Transactional
    public JobApplicationResponse updateApplicationStatus(
            Long applicationId,
            ApplicationStatus status) {

        User currentUser = getCurrentUser();

        JobApplication application =
                jobApplicationRepository
                        .findById(applicationId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Job application not found"
                                ));

        Job job = application.getJob();

        // Only job owner can accept/reject
        if (!job.getPostedBy()
                .getId()
                .equals(currentUser.getId())) {

            throw new BadRequestException(
                    "Only the job owner can update "
                            + "application status"
            );
        }

        if (status == null) {

            throw new BadRequestException(
                    "Application status is required"
            );
        }

        if (status == ApplicationStatus.PENDING) {

            throw new BadRequestException(
                    "Application status can only be "
                            + "ACCEPTED or REJECTED"
            );
        }

        application.setStatus(status);

        return jobMapper.toApplicationResponse(
                jobApplicationRepository.save(
                        application
                )
        );
    }
}