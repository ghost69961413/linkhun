package com.linkhub.repository;

import com.linkhub.entity.RepositoryFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositoryFileRepository extends JpaRepository<RepositoryFile, Long> {
    List<RepositoryFile> findByRepositoryIdAndBranchIdOrderByDirectoryDescPathAsc(Long repositoryId, Long branchId);
    Optional<RepositoryFile> findByIdAndRepositoryId(Long id, Long repositoryId);
    Optional<RepositoryFile> findByBranchIdAndPath(Long branchId, String path);
    long countByRepositoryId(Long repositoryId);
    @org.springframework.data.jpa.repository.Query("select count(f) from RepositoryFile f where f.repository.id = :repositoryId and f.branch.name = :branchName and f.directory = false")
    long countFilesByRepositoryIdAndBranch(@org.springframework.data.repository.query.Param("repositoryId") Long repositoryId, @org.springframework.data.repository.query.Param("branchName") String branchName);
    @Modifying @Query("delete from RepositoryFile f where f.repository.id = :repositoryId")
    int deleteAllByRepositoryId(@Param("repositoryId") Long repositoryId);
}
