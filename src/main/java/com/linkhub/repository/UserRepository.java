package com.linkhub.repository;

import com.linkhub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // Tumhara existing method

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    java.util.Optional<User> findByEmail(String email);

    // Search users
    Page<User> findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String username,
            String firstName,
            String lastName,
            Pageable pageable
    );
}