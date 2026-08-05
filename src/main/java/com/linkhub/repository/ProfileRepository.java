package com.linkhub.repository;

import com.linkhub.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

    Optional<Profile> findByUserUsername(String username);

    Optional<Profile> findByUserEmail(String email);

}