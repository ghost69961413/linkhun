package com.linkhub.repository;

import com.linkhub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import com.linkhub.enums.ProfileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // Tumhara existing method

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    java.util.Optional<User> findByEmail(String email);

    java.util.Optional<User> findByEmailIgnoreCase(String email);

    java.util.Optional<User> findByUsername(String username);

    java.util.Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    Page<User> findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String username,
            String firstName,
            String lastName,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "profile")
    Page<User> findByProfileProfileType(ProfileType profileType, Pageable pageable);
}
