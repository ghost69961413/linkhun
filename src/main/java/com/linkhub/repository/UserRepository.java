package com.linkhub.repository;

import com.linkhub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import com.linkhub.enums.ProfileType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // Tumhara existing method

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    java.util.Optional<User> findByEmail(String email);

    java.util.Optional<User> findByEmailIgnoreCase(String email);

    java.util.Optional<User> findByUsername(String username);

    java.util.Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    // Search users
    Page<User> findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String username,
            String firstName,
            String lastName,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "profile")
    @Query("select distinct u from User u left join u.profile p left join p.roleDetails rd where (:profileType is null or p.profileType = :profileType) and (lower(u.username) like lower(concat('%', :query, '%')) or lower(u.firstName) like lower(concat('%', :query, '%')) or lower(u.lastName) like lower(concat('%', :query, '%')) or lower(coalesce(p.headline, '')) like lower(concat('%', :query, '%')) or lower(coalesce(rd.detailsJson, '')) like lower(concat('%', :query, '%')) or exists (select s.id from Profile ps join ps.skills s where ps = p and lower(s.name) like lower(concat('%', :query, '%'))))")
    Page<User> searchProfiles(@Param("query") String query, @Param("profileType") ProfileType profileType, Pageable pageable);

    @EntityGraph(attributePaths = "profile")
    Page<User> findByProfileProfileType(ProfileType profileType, Pageable pageable);
}
