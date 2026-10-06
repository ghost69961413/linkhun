package com.linkhub.dto.profile;

import lombok.*;
import java.util.List;
import java.util.Map;
import com.linkhub.enums.ProfileType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private ProfileType profileType;
    private Map<String, Object> roleDetails;

    private Long id;
    private Long userId;

    private String headline;

    private String bio;

    private String location;

    private String website;

    private String githubUrl;

    private String linkedinUrl;

    private String portfolioUrl;

    private String company;

    private String designation;

    private Integer followers;

    private Integer following;

    private String username;

    private String firstName;

    private String lastName;

    private String profilePicture;
    private String coverImage;
    private List<String> skills;
    private List<ProfileRequest.ExperienceItem> experience;
    private List<ProfileRequest.EducationItem> education;
    private List<ProfileRequest.CertificationItem> certifications;
}
