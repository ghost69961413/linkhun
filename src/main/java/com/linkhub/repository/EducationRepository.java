package com.linkhub.repository;

import com.linkhub.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository
        extends JpaRepository<Education, Long> {

    List<Education> findByProfileId(Long profileId);

}