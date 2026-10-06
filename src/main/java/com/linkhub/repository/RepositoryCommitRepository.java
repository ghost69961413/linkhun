package com.linkhub.repository;

import com.linkhub.entity.RepositoryCommit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositoryCommitRepository extends JpaRepository<RepositoryCommit, Long> {
    List<RepositoryCommit> findTop100ByRepositoryIdOrderByCreatedAtDesc(Long repositoryId);
    List<RepositoryCommit> findTop100ByRepositoryIdAndBranchNameOrderByCreatedAtDesc(Long repositoryId, String branchName);
    @Modifying @Query("delete from RepositoryCommit c where c.repository.id = :repositoryId")
    int deleteAllByRepositoryId(@Param("repositoryId") Long repositoryId);
}
