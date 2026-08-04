package com.linkhub.service.impl;

import com.linkhub.dto.profile.ProfileRequest;
import com.linkhub.dto.profile.ProfileResponse;
import com.linkhub.entity.Profile;
import com.linkhub.mapper.ProfileMapper;
import com.linkhub.repository.ProfileRepository;
import com.linkhub.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Override
    public ProfileResponse getProfileByUsername(String username) {

        Profile profile = profileRepository
                .findByUserUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        return profileMapper.toResponse(profile);
    }

    @Override
    public ProfileResponse updateProfile(Long userId,
                                         ProfileRequest request) {

        Profile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        profile.setHeadline(request.getHeadline());
        profile.setBio(request.getBio());
        profile.setLocation(request.getLocation());
        profile.setWebsite(request.getWebsite());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setPortfolioUrl(request.getPortfolioUrl());
        profile.setCompany(request.getCompany());
        profile.setDesignation(request.getDesignation());

        profileRepository.save(profile);

        return profileMapper.toResponse(profile);
    }
}