package com.linkhub.service;

import com.linkhub.dto.profile.ProfileRequest;
import com.linkhub.dto.profile.ProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    ProfileResponse getMyProfile();

    void updateProfilePicture(
            MultipartFile image
    );

    ProfileResponse getProfileByUsername(String username);

    ProfileResponse updateProfile(ProfileRequest request);

}