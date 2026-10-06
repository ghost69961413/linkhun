package com.linkhub.controller;

import com.linkhub.dto.RepositoryDto.*;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.RepositoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/repositories")
@RequiredArgsConstructor
public class RepositoryController {
    private final RepositoryService repositoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<RepositoryResponse>> create(@Valid @RequestBody RepositoryRequest request) {
        return ok("Repository created", repositoryService.createRepository(request));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<RepositoryResponse>>> mine(@RequestParam(required=false) Long userId) {
        // The authenticated owner is the source of identity; ignore client-supplied owner ids.
        return ok("Repositories fetched", repositoryService.getMyRepositories(org.springframework.data.domain.PageRequest.of(0,100)).getContent());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RepositoryResponse>>> byUser(@PathVariable Long userId) {
        return ok("Repositories fetched", repositoryService.getRepositoriesForUser(userId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RepositoryResponse>>> publicRepositories() {
        return ok("Public repositories fetched", repositoryService.getPublicRepositories(org.springframework.data.domain.PageRequest.of(0,100)).getContent());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RepositoryResponse>>> search(@RequestParam(required=false) String name) {
        return ok("Public repositories fetched", repositoryService.searchRepositories(name, org.springframework.data.domain.PageRequest.of(0,100)).getContent());
    }

    @GetMapping("/{owner}/{name}")
    public ResponseEntity<ApiResponse<RepositoryResponse>> byOwnerAndName(@PathVariable String owner, @PathVariable String name) {
        return ok("Repository fetched", repositoryService.getRepository(owner,name));
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<ApiResponse<RepositoryResponse>> byId(@PathVariable Long id) {
        return ok("Repository fetched", repositoryService.getRepository(id));
    }

    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<ApiResponse<RepositoryResponse>> update(@PathVariable Long id, @Valid @RequestBody RepositoryRequest request) {
        return ok("Repository updated", repositoryService.updateRepository(id,request));
    }

    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        repositoryService.deleteRepository(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Repository deleted").build());
    }

    @GetMapping("/{id:[0-9]+}/files")
    public ResponseEntity<ApiResponse<List<RepositoryFileResponse>>> files(@PathVariable Long id, @RequestParam(required=false) String branch) {
        return ok("Files fetched", repositoryService.getFiles(id,branch));
    }

    @GetMapping("/{id:[0-9]+}/files/{fileId:[0-9]+}")
    public ResponseEntity<ApiResponse<RepositoryFileResponse>> file(@PathVariable Long id, @PathVariable Long fileId) {
        return ok("File fetched", repositoryService.getFile(id,fileId));
    }

    @GetMapping("/{id:[0-9]+}/files/{fileId:[0-9]+}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id, @PathVariable Long fileId) {
        RepositoryFileResponse meta = repositoryService.getFile(id,fileId);
        MediaType type = MediaType.APPLICATION_OCTET_STREAM;
        try { if (meta.getMimeType() != null) type = MediaType.parseMediaType(meta.getMimeType()); } catch (Exception ignored) { }
        String filename = Path.of(meta.getPath()).getFileName().toString();
        return ResponseEntity.ok().contentType(type).header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString()).body(repositoryService.downloadFile(id,fileId));
    }

    @PostMapping("/{id:[0-9]+}/files")
    public ResponseEntity<ApiResponse<RepositoryFileResponse>> createFile(@PathVariable Long id, @RequestParam(required=false) String branch, @Valid @RequestBody RepositoryFileRequest request) {
        return ok("File saved", repositoryService.createFile(id,branch,request));
    }

    @PostMapping("/{id:[0-9]+}/files/upload")
    public ResponseEntity<ApiResponse<RepositoryFileResponse>> uploadFile(@PathVariable Long id, @RequestParam String path, @RequestParam(required=false) String branch, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new com.linkhub.exception.BadRequestException("Choose a non-empty source file to upload.");
        if (file.getSize() > 2L * 1024 * 1024) throw new com.linkhub.exception.BadRequestException("Source files must be 2 MB or smaller.");
        byte[] bytes = file.getBytes();
        String content = new String(bytes, StandardCharsets.UTF_8);
        boolean binary = content.indexOf('\uFFFD') >= 0 || content.indexOf('\0') >= 0;
        if (binary) content = java.util.Base64.getEncoder().encodeToString(bytes);
        return ok("File uploaded", repositoryService.uploadFile(id,branch,path,content,binary,file.getContentType()));
    }

    @PutMapping("/{id:[0-9]+}/files/{fileId:[0-9]+}")
    public ResponseEntity<ApiResponse<RepositoryFileResponse>> updateFile(@PathVariable Long id, @PathVariable Long fileId, @RequestBody RepositoryFileRequest request) {
        return ok("File updated", repositoryService.updateFile(id,fileId,request));
    }

    @DeleteMapping("/{id:[0-9]+}/files/{fileId:[0-9]+}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable Long id, @PathVariable Long fileId) {
        repositoryService.deleteFile(id,fileId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("File deleted").build());
    }

    @GetMapping("/{id:[0-9]+}/branches")
    public ResponseEntity<ApiResponse<List<String>>> branches(@PathVariable Long id) { return ok("Branches fetched",repositoryService.getBranches(id)); }

    @PostMapping("/{id:[0-9]+}/branches")
    public ResponseEntity<ApiResponse<String>> createBranch(@PathVariable Long id,@Valid @RequestBody RepositoryBranchRequest request) { return ok("Branch created",repositoryService.createBranch(id,request)); }

    @GetMapping("/{id:[0-9]+}/commits")
    public ResponseEntity<ApiResponse<List<RepositoryCommitResponse>>> commits(@PathVariable Long id,@RequestParam(required=false) String branch) { return ok("Commits fetched",repositoryService.getCommits(id,branch)); }

    private static <T> ResponseEntity<ApiResponse<T>> ok(String message,T data) {
        return ResponseEntity.ok(ApiResponse.<T>builder().success(true).message(message).data(data).build());
    }
}
