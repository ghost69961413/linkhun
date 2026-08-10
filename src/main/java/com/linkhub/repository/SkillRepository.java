package com.linkhub.repository;

import com.linkhub.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    Page<Skill> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );

}