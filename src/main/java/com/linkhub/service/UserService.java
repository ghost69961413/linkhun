package com.linkhub.service;

import com.linkhub.dto.UserDto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse getCurrentUser();

    UserResponse getUserById(Long userId);

    UserResponse getUserByUsername(String username);

    Page<UserResponse> searchUsers(
            String keyword,
            Pageable pageable
    );
}