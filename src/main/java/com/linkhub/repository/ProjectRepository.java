package com.linkhub.repository;

import com.linkhub.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByProfileId(Long profileId);

    Page<Project> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title,
            String description,
            Pageable pageable
    );

}