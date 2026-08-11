package com.linkhub.repository;

import com.linkhub.entity.Job;
import com.linkhub.entity.JobApplication;
import com.linkhub.entity.User;
import com.linkhub.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    boolean existsByJobAndApplicant(
            Job job,
            User applicant
    );

    Page<JobApplication> findByApplicant(
            User applicant,
            Pageable pageable
    );

    Page<JobApplication> findByJob(
            Job job,
            Pageable pageable
    );

    Page<JobApplication> findByJobAndStatus(
            Job job,
            ApplicationStatus status,
            Pageable pageable
    );

    Optional<JobApplication> findByJobAndApplicant(
            Job job,
            User applicant
    );

    long countByJob(Job job);

    long countByJobAndStatus(
            Job job,
            ApplicationStatus status
    );
}