package com.linkhub.repository;

import com.linkhub.entity.RepositoryBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositoryBranchRepository extends JpaRepository<RepositoryBranch, Long> {
    List<RepositoryBranch> findByRepositoryIdOrderByNameAsc(Long repositoryId);
    Optional<RepositoryBranch> findByRepositoryIdAndName(Long repositoryId, String name);
    Optional<RepositoryBranch> findByRepositoryIdAndDefaultBranchTrue(Long repositoryId);
    boolean existsByRepositoryIdAndNameIgnoreCase(Long repositoryId, String name);
    @Modifying @Query("delete from RepositoryBranch b where b.repository.id = :repositoryId")
    int deleteAllByRepositoryId(@Param("repositoryId") Long repositoryId);
}
