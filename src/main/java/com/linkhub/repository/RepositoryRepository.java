package com.linkhub.repository;

import com.linkhub.entity.Repository;
import com.linkhub.entity.User;
import com.linkhub.enums.RepositoryVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
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

    Page<Repository> findByVisibilityAndNameContainingIgnoreCase(RepositoryVisibility visibility, String name, Pageable pageable);

    List<Repository> findByOwnerIdOrderByUpdatedAtDesc(Long ownerId);
    List<Repository> findByOwnerIdAndVisibilityOrderByUpdatedAtDesc(Long ownerId, RepositoryVisibility visibility);
    java.util.Optional<Repository> findByOwnerUsernameIgnoreCaseAndNameIgnoreCase(String username, String name);
    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);
}
