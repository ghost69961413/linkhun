package com.linkhub.controller;

import com.linkhub.dto.profile.ProfileRequest;
import com.linkhub.dto.profile.ProfileResponse;
import com.linkhub.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{username}")
    public ProfileResponse getProfile(
            @PathVariable String username) {

        return profileService.getProfileByUsername(username);
    }

    @PutMapping("/{userId}")
    public ProfileResponse updateProfile(
            @PathVariable Long userId,
            @RequestBody ProfileRequest request) {

        return profileService.updateProfile(userId, request);
    }
}