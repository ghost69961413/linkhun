package com.linkhub.service.impl;

import com.linkhub.dto.profile.ProfileRequest;
import com.linkhub.dto.profile.ProfileResponse;
import com.linkhub.entity.Profile;
import com.linkhub.entity.User;
import com.linkhub.exception.ProfileNotFoundException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.ProfileMapper;
import com.linkhub.repository.ProfileRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    @Override
    public ProfileResponse getMyProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return profileMapper.toResponse(profile);
    }

    @Override
    public ProfileResponse getProfileByUsername(String username) {

        Profile profile = profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return profileMapper.toResponse(profile);
    }

    @Override
    public ProfileResponse updateProfile(ProfileRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new ProfileNotFoundException("Profile not found"));
        profileMapper.updateProfile(request, profile);

        profileRepository.save(profile);

        return profileMapper.toResponse(profile);
    }
}