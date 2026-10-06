package com.linkhub.service;

import com.linkhub.dto.RepositoryDto.RepositoryRequest;
import com.linkhub.dto.RepositoryDto.RepositoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.linkhub.dto.RepositoryDto.RepositoryFileRequest;
import com.linkhub.dto.RepositoryDto.RepositoryFileResponse;
import com.linkhub.dto.RepositoryDto.RepositoryCommitResponse;
import com.linkhub.dto.RepositoryDto.RepositoryBranchRequest;
import java.util.List;

public interface RepositoryService {

    RepositoryResponse createRepository(RepositoryRequest request);

    Page<RepositoryResponse> getMyRepositories(Pageable pageable);

    Page<RepositoryResponse> getPublicRepositories(Pageable pageable);

    RepositoryResponse getRepository(Long id);

    Page<RepositoryResponse> searchRepositories(
            String name,
            Pageable pageable
    );

    RepositoryResponse updateRepository(
            Long id,
            RepositoryRequest request
    );

    void deleteRepository(Long id);

    RepositoryResponse getRepository(String owner, String name);
    List<RepositoryResponse> getRepositoriesForUser(Long userId);
    List<RepositoryFileResponse> getFiles(Long id, String branch);
    RepositoryFileResponse getFile(Long id, Long fileId);
    RepositoryFileResponse createFile(Long id, String branch, RepositoryFileRequest request);
    RepositoryFileResponse updateFile(Long id, Long fileId, RepositoryFileRequest request);
    void deleteFile(Long id, Long fileId);
    RepositoryFileResponse uploadFile(Long id, String branch, String path, String content);
    RepositoryFileResponse uploadFile(Long id, String branch, String path, String content, boolean binary, String mimeType);
    byte[] downloadFile(Long id, Long fileId);
    List<RepositoryCommitResponse> getCommits(Long id, String branch);
    List<String> getBranches(Long id);
    String createBranch(Long id, RepositoryBranchRequest request);
}
