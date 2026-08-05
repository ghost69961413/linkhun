package com.linkhub.service;

import com.linkhub.dto.profile.ProfileRequest;
import com.linkhub.dto.profile.ProfileResponse;

public interface ProfileService {

    ProfileResponse getMyProfile();

    ProfileResponse getProfileByUsername(String username);

    ProfileResponse updateProfile(ProfileRequest request);

}