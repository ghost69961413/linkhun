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

    @GetMapping("/{userId}")
    public ProfileResponse getProfile(@PathVariable String userId) {
        return profileService.getProfile(userId);
    }

    @PutMapping({"", "/me"})
    public ProfileResponse updateProfile(@RequestBody ProfileRequest request) {
        return profileService.updateProfile(request);
    }
    @PostMapping("/picture")
    public ResponseEntity<ApiResponse<String>> updateProfilePicture(
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        MultipartFile selected = image != null ? image : file;
        String url = profileService.updateProfileImage(selected, false);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true).message("Profile image updated successfully").data(url).build());
    }

    @PostMapping("/cover")
    public ResponseEntity<ApiResponse<String>> updateCoverImage(@RequestParam("image") MultipartFile image) {
        String url = profileService.updateProfileImage(image, true);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true).message("Cover image updated successfully").data(url).build());
    }
}
