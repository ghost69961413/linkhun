package com.linkhub.repository;

import com.linkhub.entity.Job;
import com.linkhub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByActiveTrue(Pageable pageable);

    Page<Job> findByPostedByAndActiveTrue(
            User postedBy,
            Pageable pageable
    );

    Page<Job> findByTitleContainingIgnoreCaseAndActiveTrue(
            String title,
            Pageable pageable
    );

    Page<Job> findByLocationContainingIgnoreCaseAndActiveTrue(
            String location,
            Pageable pageable
    );

    Page<Job> findByJobTypeAndActiveTrue(
            com.linkhub.enums.JobType jobType,
            Pageable pageable
    );
}