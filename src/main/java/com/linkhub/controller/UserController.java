package com.linkhub.controller;

import com.linkhub.dto.UserDto.UserResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.UserService;
import com.linkhub.dto.UserDto.ChangeEmailRequest;
import com.linkhub.dto.auth.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/change-email")
    public ResponseEntity<AuthResponse> changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        return ResponseEntity.ok(userService.changeEmail(request));
    }

    // =====================================================
    // CURRENT USER
    // =====================================================

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {

        UserResponse response =
                userService.getCurrentUser();

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("Current user fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // =====================================================
    // GET USER BY ID
    // =====================================================

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long userId) {

        UserResponse response =
                userService.getUserById(userId);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("User fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // =====================================================
    // GET USER BY USERNAME
    // =====================================================

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>>
    getUserByUsername(
            @PathVariable String username) {

        UserResponse response =
                userService.getUserByUsername(username);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("User fetched successfully")
                        .data(response)
                        .build()
        );
    }

    // =====================================================
    // SEARCH USERS
    // =====================================================

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserResponse>>>
    searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("username").ascending()
        );

        Page<UserResponse> response =
                userService.searchUsers(
                        keyword,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.<Page<UserResponse>>builder()
                        .success(true)
                        .message("Users searched successfully")
                        .data(response)
                        .build()
        );
    }
}
