package com.linkhub.service.impl;

import com.linkhub.dto.RepositoryDto.*;
import com.linkhub.entity.*;
import com.linkhub.enums.RepositoryVisibility;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.RepositoryNotFoundException;
import com.linkhub.exception.ResourceAlreadyExistsException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.RepositoryMapper;
import com.linkhub.repository.*;
import com.linkhub.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.regex.Pattern;

@Service @RequiredArgsConstructor @Transactional
public class RepositoryServiceImpl implements RepositoryService {
    private static final Pattern REPO_NAME = Pattern.compile("^[A-Za-z0-9._-]{1,100}$");
    private static final int MAX_TEXT_FILE_CHARS = 2_000_000;
    private final RepositoryRepository repositoryRepository;
    private final RepositoryBranchRepository branchRepository;
    private final RepositoryFileRepository fileRepository;
    private final RepositoryCommitRepository commitRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RepositoryMapper repositoryMapper;

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal()))
            throw new AccessDeniedException("Sign in to access LinkHub repositories.");
        return userRepository.findByEmail(auth.getName()).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Repository requireVisible(Long id) {
        Repository repository = repositoryRepository.findById(id).orElseThrow(() -> new RepositoryNotFoundException("Repository not found"));
        User user = currentUser();
        if (repository.getVisibility() == RepositoryVisibility.PRIVATE && !repository.getOwner().getId().equals(user.getId()))
            throw new RepositoryNotFoundException("Repository not found");
        return repository;
    }

    private Repository requireOwner(Long id) {
        Repository repository = repositoryRepository.findById(id).orElseThrow(() -> new RepositoryNotFoundException("Repository not found"));
        if (!repository.getOwner().getId().equals(currentUser().getId())) throw new AccessDeniedException("Only the repository owner can change repository contents.");
        initializeFileCount(repository);
        return repository;
    }

    private void initializeFileCount(Repository repository) {
        if (repository.getFilesCount() == null || repository.getFilesCount() == 0) {
            long actual = fileRepository.countFilesByRepositoryIdAndBranch(repository.getId(), repository.getDefaultBranch());
            if (actual > 0) repository.setFilesCount((int) actual);
        }
    }

    private RepositoryResponse toResponse(Repository repository) {
        RepositoryResponse response = repositoryMapper.toResponse(repository);
        if (response.getFilesCount() == null || response.getFilesCount() == 0) {
            response.setFilesCount((int) fileRepository.countFilesByRepositoryIdAndBranch(repository.getId(), repository.getDefaultBranch()));
        }
        return response;
    }

    private RepositoryBranch branch(Repository repository, String name) {
        String selected = name == null || name.isBlank() ? repository.getDefaultBranch() : name.trim();
        return branchRepository.findByRepositoryIdAndName(repository.getId(), selected)
                .orElseThrow(() -> new RepositoryNotFoundException("Branch not found"));
    }

    @Override public RepositoryResponse createRepository(RepositoryRequest request) {
        User owner = currentUser();
        String name = request.getName() == null ? "" : request.getName().trim();
        if (!REPO_NAME.matcher(name).matches()) throw new BadRequestException("Repository name must be 1–100 characters using letters, numbers, dot, underscore, or hyphen.");
        if (repositoryRepository.existsByOwnerIdAndNameIgnoreCase(owner.getId(), name)) throw new ResourceAlreadyExistsException("You already have a repository with that name.");
        Repository repo = Repository.builder().name(name).description(clean(request.getDescription())).repositoryUrl(clean(request.getRepositoryUrl())).language(null).visibility(request.getVisibility() == null ? RepositoryVisibility.PUBLIC : request.getVisibility()).initializeReadme(!Boolean.FALSE.equals(request.getInitializeReadme())).license(clean(request.getLicense())).gitignoreTemplate(clean(request.getGitignoreTemplate())).defaultBranch("main").owner(owner).build();
        repo = repositoryRepository.save(repo);
        RepositoryBranch main = branchRepository.save(RepositoryBranch.builder().name("main").defaultBranch(true).repository(repo).build());
        List<String> summaries = new ArrayList<>();
        if (Boolean.TRUE.equals(repo.getInitializeReadme())) {
            String readme = "# " + name + "\n\n" + (repo.getDescription() == null ? "" : repo.getDescription() + "\n\n") + "Created on LinkHub.\n";
            saveFile(repo, main, "README.md", readme, false);
            summaries.add("Added README.md");
        }
        if (repo.getLicense() != null && !repo.getLicense().equalsIgnoreCase("none")) {
            if (!repo.getLicense().equalsIgnoreCase("MIT")) throw new BadRequestException("Choose MIT or None for the repository license.");
            saveFile(repo, main, "LICENSE", licenseText(repo.getLicense()), false);
            summaries.add("Added " + repo.getLicense() + " license");
        }
        String ignore = gitignoreText(repo.getGitignoreTemplate());
        if (ignore != null) { saveFile(repo, main, ".gitignore", ignore, false); summaries.add("Added .gitignore"); }
        commit(repo, main, owner, "Initial commit", String.join(" · ", summaries));
        return toResponse(repo);
    }

    private String licenseText(String license) {
        if ("MIT".equalsIgnoreCase(license)) return "MIT License\n\nCopyright (c) " + java.time.Year.now().getValue() + " " + currentUser().getFirstName() + "\n\nPermission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n\nThe above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n\nTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED.\n";
        return "";
    }

    private String gitignoreText(String template) {
        if (template == null || template.isBlank() || "none".equalsIgnoreCase(template)) return null;
        return switch (template.toLowerCase(Locale.ROOT)) {
            case "node", "node.js" -> "node_modules/\ndist/\n.env\n.DS_Store\n";
            case "java", "maven" -> "target/\n*.class\n.env\n.DS_Store\n";
            case "python" -> "__pycache__/\n*.py[cod]\n.venv/\n.env\n";
            case "react", "react/ts" -> "node_modules/\ndist/\n.env\n.DS_Store\n";
            default -> throw new BadRequestException("Choose a supported .gitignore template or None.");
        };
    }

    @Override @Transactional(readOnly = true)
    public Page<RepositoryResponse> getMyRepositories(Pageable pageable) { return repositoryRepository.findByOwner(currentUser(), pageable).map(this::toResponse); }
    @Override @Transactional(readOnly = true)
    public Page<RepositoryResponse> getPublicRepositories(Pageable pageable) { return repositoryRepository.findByVisibility(RepositoryVisibility.PUBLIC, pageable).map(this::toResponse); }
    @Override @Transactional(readOnly = true)
    public Page<RepositoryResponse> searchRepositories(String name, Pageable pageable) {
        if (name == null || name.isBlank()) return getPublicRepositories(pageable);
        return repositoryRepository.findByVisibilityAndNameContainingIgnoreCase(RepositoryVisibility.PUBLIC, name.trim(), pageable).map(this::toResponse);
    }
    @Override @Transactional(readOnly = true)
    public RepositoryResponse getRepository(Long id) { return toResponse(requireVisible(id)); }
    @Override @Transactional(readOnly = true)
    public RepositoryResponse getRepository(String owner, String name) {
        Repository repo = repositoryRepository.findByOwnerUsernameIgnoreCaseAndNameIgnoreCase(owner, name).orElseThrow(() -> new RepositoryNotFoundException("Repository not found"));
        if (repo.getVisibility() == RepositoryVisibility.PRIVATE && !repo.getOwner().getId().equals(currentUser().getId())) throw new RepositoryNotFoundException("Repository not found");
        return toResponse(repo);
    }
    @Override @Transactional(readOnly = true)
    public List<RepositoryResponse> getRepositoriesForUser(Long userId) {
        User viewer = currentUser();
        List<Repository> repos = viewer.getId().equals(userId) ? repositoryRepository.findByOwnerIdOrderByUpdatedAtDesc(userId) : repositoryRepository.findByOwnerIdAndVisibilityOrderByUpdatedAtDesc(userId, RepositoryVisibility.PUBLIC);
        return repos.stream().map(this::toResponse).toList();
    }
    @Override
    public RepositoryResponse updateRepository(Long id, RepositoryRequest request) {
        Repository repo = requireOwner(id);
        String name = request.getName() == null ? "" : request.getName().trim();
        if (!REPO_NAME.matcher(name).matches()) throw new BadRequestException("Repository name is invalid.");
        if (!repo.getName().equalsIgnoreCase(name) && repositoryRepository.existsByOwnerIdAndNameIgnoreCase(repo.getOwner().getId(), name)) throw new ResourceAlreadyExistsException("You already have a repository with that name.");
        repo.setName(name); repo.setDescription(clean(request.getDescription())); repo.setRepositoryUrl(clean(request.getRepositoryUrl()));
        if (request.getVisibility() != null) repo.setVisibility(request.getVisibility());
        repo.setLicense(clean(request.getLicense())); repo.setGitignoreTemplate(clean(request.getGitignoreTemplate()));
        return toResponse(repositoryRepository.save(repo));
    }
    @Override public void deleteRepository(Long id) {
        requireOwner(id);
        projectRepository.clearRepositoryLink(id);
        commitRepository.deleteAllByRepositoryId(id);
        fileRepository.deleteAllByRepositoryId(id);
        branchRepository.deleteAllByRepositoryId(id);
        repositoryRepository.deleteById(id);
    }

    @Override @Transactional(readOnly = true)
    public List<RepositoryFileResponse> getFiles(Long id, String branchName) {
        Repository repo = requireVisible(id); RepositoryBranch branch = branch(repo, branchName);
        return fileRepository.findByRepositoryIdAndBranchIdOrderByDirectoryDescPathAsc(id, branch.getId()).stream().map(f -> fileResponse(f, false)).toList();
    }
    @Override @Transactional(readOnly = true)
    public RepositoryFileResponse getFile(Long id, Long fileId) {
        requireVisible(id); RepositoryFile file = fileRepository.findByIdAndRepositoryId(fileId, id).orElseThrow(() -> new RepositoryNotFoundException("File not found")); return fileResponse(file, true);
    }
    @Override public RepositoryFileResponse createFile(Long id, String branchName, RepositoryFileRequest request) {
        Repository repo = requireOwner(id); RepositoryBranch branch = branch(repo, branchName);
        RepositoryFile saved = saveFile(repo, branch, normalizePath(request.getPath()), request.getContent(), Boolean.TRUE.equals(request.getDirectory()), false, null, false);
        commit(repo, branch, currentUser(), defaultMessage(request.getCommitMessage(), (saved.getDirectory() ? "Create folder " : "Add ") + saved.getPath()), (saved.getDirectory() ? "Created folder " : "Added file ") + saved.getPath());
        return fileResponse(saved);
    }
    @Override public RepositoryFileResponse uploadFile(Long id, String branchName, String path, String content) {
        return uploadFile(id, branchName, path, content, false, "text/plain");
    }
    @Override public RepositoryFileResponse uploadFile(Long id, String branchName, String path, String content, boolean binary, String mimeType) {
        Repository repo = requireOwner(id); RepositoryBranch branch = branch(repo, branchName);
        RepositoryFile saved = saveFile(repo, branch, normalizePath(path), content, false, binary, mimeType, true);
        commit(repo, branch, currentUser(), "Upload " + saved.getPath(), "Uploaded file " + saved.getPath()); return fileResponse(saved);
    }
    @Override public RepositoryFileResponse updateFile(Long id, Long fileId, RepositoryFileRequest request) {
        Repository repo = requireOwner(id); RepositoryFile file = fileRepository.findByIdAndRepositoryId(fileId, id).orElseThrow(() -> new RepositoryNotFoundException("File not found"));
        if (Boolean.TRUE.equals(file.getDirectory())) throw new BadRequestException("Folders do not have editable content.");
        if (Boolean.TRUE.equals(file.getBinary())) throw new BadRequestException("Download the binary file and upload a replacement to edit it.");
        if (request.getContent() == null) throw new BadRequestException("File content is required.");
        if (request.getContent().length() > MAX_TEXT_FILE_CHARS) throw new BadRequestException("Text files must be 2 MB or smaller.");
        file.setContent(request.getContent()); file.setBinary(false); file.setMimeType("text/plain; charset=utf-8"); file = fileRepository.save(file);
        commit(repo, file.getBranch(), currentUser(), defaultMessage(request.getCommitMessage(), "Update " + file.getPath()), "Updated file " + file.getPath()); return fileResponse(file);
    }
    @Override public void deleteFile(Long id, Long fileId) {
        Repository repo = requireOwner(id); RepositoryFile file = fileRepository.findByIdAndRepositoryId(fileId, id).orElseThrow(() -> new RepositoryNotFoundException("File not found"));
        String path = file.getPath(); RepositoryBranch branch = file.getBranch();
        if (Boolean.TRUE.equals(file.getDirectory())) {
            String prefix = path + "/";
            int removedFiles=0;
            for (RepositoryFile child : fileRepository.findByRepositoryIdAndBranchIdOrderByDirectoryDescPathAsc(id, branch.getId())) if (child.getPath().startsWith(prefix)) { if (!Boolean.TRUE.equals(child.getDirectory())) removedFiles++; fileRepository.delete(child); }
            if (branch.getName().equals(repo.getDefaultBranch()) && removedFiles>0) repo.setFilesCount(Math.max(0,(repo.getFilesCount()==null?0:repo.getFilesCount())-removedFiles));
        } else if (branch.getName().equals(repo.getDefaultBranch())) {
            repo.setFilesCount(Math.max(0,(repo.getFilesCount()==null?0:repo.getFilesCount())-1));
        }
        fileRepository.delete(file); commit(repo, branch, currentUser(), "Delete " + path, "Deleted " + path);
    }
    @Override @Transactional(readOnly = true)
    public List<RepositoryCommitResponse> getCommits(Long id, String branchName) {
        Repository repo = requireVisible(id);
        List<RepositoryCommit> commits = branchName == null || branchName.isBlank() ? commitRepository.findTop100ByRepositoryIdOrderByCreatedAtDesc(id) : commitRepository.findTop100ByRepositoryIdAndBranchNameOrderByCreatedAtDesc(id, branchName);
        return commits.stream().map(c -> RepositoryCommitResponse.builder().id(c.getId()).message(c.getMessage()).changeSummary(c.getChangeSummary()).branch(c.getBranch().getName()).author(c.getAuthor().getUsername()).createdAt(c.getCreatedAt()).build()).toList();
    }
    @Override @Transactional(readOnly = true)
    public List<String> getBranches(Long id) { requireVisible(id); return branchRepository.findByRepositoryIdOrderByNameAsc(id).stream().map(RepositoryBranch::getName).toList(); }
    @Override public String createBranch(Long id, RepositoryBranchRequest request) {
        Repository repo = requireOwner(id); String name = request.getName().trim();
        if (name.length() > 100 || !name.matches("^[A-Za-z0-9._-]+(?:/[A-Za-z0-9._-]+)*$") || name.contains("..")) throw new BadRequestException("Branch name is invalid.");
        if (branchRepository.existsByRepositoryIdAndNameIgnoreCase(id, name)) throw new ResourceAlreadyExistsException("Branch already exists.");
        RepositoryBranch base = branch(repo, request.getFromBranch());
        RepositoryBranch created = branchRepository.save(RepositoryBranch.builder().name(name).defaultBranch(false).repository(repo).build());
        for (RepositoryFile f : fileRepository.findByRepositoryIdAndBranchIdOrderByDirectoryDescPathAsc(id, base.getId())) saveFile(repo, created, f.getPath(), f.getContent(), f.getDirectory(), Boolean.TRUE.equals(f.getBinary()), f.getMimeType(), true);
        commit(repo, created, currentUser(), "Create branch " + name, "Created from " + base.getName()); return name;
    }

    private RepositoryFile saveFile(Repository repo, RepositoryBranch branch, String path, String content, boolean directory) {
        return saveFile(repo,branch,path,content,directory,false,null,false);
    }
    private RepositoryFile saveFile(Repository repo, RepositoryBranch branch, String path, String content, boolean directory, boolean binary, String mimeType, boolean allowReplace) {
        if (!directory && content == null) throw new BadRequestException("File content is required.");
        if (!binary && content != null && content.length() > MAX_TEXT_FILE_CHARS) throw new BadRequestException("Text files must be 2 MB or smaller.");
        String[] parts = path.split("/"); StringBuilder parent = new StringBuilder();
        for (int i=0;i<parts.length-1;i++) {
            if (parent.length()>0) parent.append('/'); parent.append(parts[i]);
            String parentPath = parent.toString();
            RepositoryFile existingParent = fileRepository.findByBranchIdAndPath(branch.getId(), parentPath).orElse(null);
            if (existingParent != null && !Boolean.TRUE.equals(existingParent.getDirectory())) throw new BadRequestException("A file already exists in the requested folder path.");
            if (existingParent == null) fileRepository.save(RepositoryFile.builder().path(parentPath).directory(true).repository(repo).branch(branch).build());
        }
        RepositoryFile existing = fileRepository.findByBranchIdAndPath(branch.getId(), path).orElse(null);
        if (existing != null && Boolean.TRUE.equals(existing.getDirectory()) != directory) throw new BadRequestException("A file or folder already exists at that path.");
        if (existing != null && existing.getId() != null && !directory && !allowReplace && !Objects.equals(existing.getContent(), content)) throw new ResourceAlreadyExistsException("A file already exists at that path. Edit the existing file instead.");
        RepositoryFile file = existing == null ? RepositoryFile.builder().path(path).repository(repo).branch(branch).build() : existing;
        file.setDirectory(directory); file.setContent(directory ? null : content); file.setBinary(!directory && binary); file.setMimeType(directory ? null : mimeType);
        boolean addToDefaultBranchCount = existing == null && !directory && branch.getName().equals(repo.getDefaultBranch());
        RepositoryFile saved = fileRepository.save(file);
        if (addToDefaultBranchCount) repo.setFilesCount((repo.getFilesCount()==null?0:repo.getFilesCount())+1);
        if (repo.getFiles().stream().noneMatch(current -> Objects.equals(current.getId(), saved.getId()))) repo.getFiles().add(saved);
        return saved;
    }
    private void commit(Repository repo, RepositoryBranch branch, User author, String message, String summary) { commitRepository.save(RepositoryCommit.builder().repository(repo).branch(branch).author(author).message(limit(message,200)).changeSummary(limit(summary,2000)).build()); }
    private RepositoryFileResponse fileResponse(RepositoryFile f) { return fileResponse(f, true); }
    private RepositoryFileResponse fileResponse(RepositoryFile f, boolean includeContent) {
        boolean binary=Boolean.TRUE.equals(f.getBinary());
        long size=f.getContent()==null?0L:binary?java.util.Base64.getDecoder().decode(f.getContent()).length:(long)f.getContent().getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        return RepositoryFileResponse.builder().id(f.getId()).path(f.getPath()).directory(f.getDirectory()).binary(binary).mimeType(f.getMimeType()).size(size).updatedAt(f.getUpdatedAt()).content(includeContent&&!binary?f.getContent():null).build();
    }
    @Override @Transactional(readOnly = true)
    public byte[] downloadFile(Long id, Long fileId) {
        requireVisible(id); RepositoryFile file=fileRepository.findByIdAndRepositoryId(fileId,id).orElseThrow(()->new RepositoryNotFoundException("File not found"));
        if(Boolean.TRUE.equals(file.getDirectory())) throw new BadRequestException("Folders cannot be downloaded.");
        return Boolean.TRUE.equals(file.getBinary())?java.util.Base64.getDecoder().decode(file.getContent()):file.getContent().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
    private static String normalizePath(String raw) {
        if (raw == null) throw new BadRequestException("File or folder path is required.");
        String path = raw.trim().replace('\\','/');
        if (path.startsWith("/") || path.length()>1000 || path.isBlank()) throw new BadRequestException("File path is invalid.");
        for (String part : path.split("/")) if (part.isBlank() || part.equals(".") || part.equals("..") || part.indexOf('\0')>=0) throw new BadRequestException("File path is invalid.");
        return path;
    }
    private static String defaultMessage(String requested, String fallback) { return requested==null||requested.isBlank()?fallback:requested.trim(); }
    private static String clean(String s) { return s==null||s.isBlank()?null:s.trim(); }
    private static String limit(String s,int max) { return s==null?null:s.substring(0,Math.min(max,s.length())); }
}
