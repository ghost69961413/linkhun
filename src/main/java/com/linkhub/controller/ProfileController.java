package com.linkhub.controller;

import com.linkhub.dto.profile.ProfileRequest;
import com.linkhub.dto.profile.ProfileResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ProfileResponse getMyProfile() {
        return profileService.getMyProfile();
    }

    @GetMapping("/{username}")
    public ProfileResponse getProfile(@PathVariable String username) {
        return profileService.getProfileByUsername(username);
    }

    @PutMapping
    public ProfileResponse updateProfile(@RequestBody ProfileRequest request) {
        return profileService.updateProfile(request);
    }
    @PostMapping("/picture")
    public ResponseEntity<ApiResponse<Void>> updateProfilePicture(
            @RequestParam("image") MultipartFile image) {

        profileService.updateProfilePicture(image);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Profile picture updated successfully")
                        .build()
        );
    }
}