package com.linkhub.repository;

import com.linkhub.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByProfileId(Long profileId);

    Page<Project> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title,
            String description,
            Pageable pageable
    );

    @Modifying @Query("update Project p set p.linkedRepository = null where p.linkedRepository.id = :repositoryId")
    int clearRepositoryLink(@Param("repositoryId") Long repositoryId);

    @Query("select p from Project p where p.profile.user.id = :ownerId and (:viewerId = :ownerId or upper(coalesce(p.visibility, 'PUBLIC')) = 'PUBLIC')")
    List<Project> findVisibleForUser(@Param("ownerId") Long ownerId, @Param("viewerId") Long viewerId);

    @Query("select p from Project p where upper(coalesce(p.visibility, 'PUBLIC')) = 'PUBLIC' and (lower(p.title) like lower(concat('%', :query, '%')) or lower(coalesce(p.description, '')) like lower(concat('%', :query, '%')))")
    Page<Project> searchPublicProjects(@Param("query") String query, Pageable pageable);

}
