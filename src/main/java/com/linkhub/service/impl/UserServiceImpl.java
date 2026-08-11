package com.linkhub.service.impl;

import com.linkhub.dto.UserDto.UserResponse;
import com.linkhub.entity.User;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.UserMapper;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // =====================================================
    // CURRENT USER
    // =====================================================

    private User getCurrentUserEntity() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));
    }

    // =====================================================
    // GET CURRENT USER
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {

        User user = getCurrentUserEntity();

        return userMapper.toResponse(user);
    }

    // =====================================================
    // GET USER BY ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        return userMapper.toResponse(user);
    }

    // =====================================================
    // GET USER BY USERNAME
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(
            String username) {

        User user = userRepository
                .findAll()
                .stream()
                .filter(u ->
                        u.getUsername()
                                .equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        return userMapper.toResponse(user);
    }

    // =====================================================
    // SEARCH USERS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(
            String keyword,
            Pageable pageable) {

        String searchKeyword =
                keyword == null
                        ? ""
                        : keyword.trim();

        return userRepository
                .findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        searchKeyword,
                        searchKeyword,
                        searchKeyword,
                        pageable
                )
                .map(userMapper::toResponse);
    }
}