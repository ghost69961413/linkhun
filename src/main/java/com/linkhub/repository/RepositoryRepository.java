package com.linkhub.repository;

import com.linkhub.entity.Repository;
import com.linkhub.entity.User;
import com.linkhub.enums.RepositoryVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryRepository
        extends JpaRepository<Repository, Long> {

    Page<Repository> findByOwner(
            User owner,
            Pageable pageable
    );

    Page<Repository> findByVisibility(
            RepositoryVisibility visibility,
            Pageable pageable
    );

    Page<Repository> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );

    Page<Repository> findByOwnerAndVisibility(
            User owner,
            RepositoryVisibility visibility,
            Pageable pageable
    );
}