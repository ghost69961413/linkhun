package com.linkhub.service;

import com.linkhub.dto.profile.ProfileRequest;
import com.linkhub.dto.profile.ProfileResponse;

public interface ProfileService {

    ProfileResponse getProfileByUsername(String username);

    ProfileResponse updateProfile(
            Long userId,
            ProfileRequest request
    );

}