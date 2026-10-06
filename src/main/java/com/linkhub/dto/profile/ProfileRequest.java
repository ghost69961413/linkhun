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
public class ProfileRequest {

    private ProfileType profileType;
    private Map<String, Object> roleDetails;

    private String headline;

    private String bio;

    private String location;

    private String website;

    private String githubUrl;

    private String linkedinUrl;

    private String portfolioUrl;

    private String company;

    private String designation;

    private String username;
    private String fullName;
    private String coverImage;
    private List<String> skills;
    private List<ExperienceItem> experience;
    private List<EducationItem> education;
    private List<CertificationItem> certifications;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ExperienceItem {
        private String company;
        private String title;
        private String employmentType;
        private String location;
        private String startYear;
        private String endYear;
        private Boolean current;
        private String description;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class EducationItem {
        private String school;
        private String degree;
        private String fieldOfStudy;
        private String startYear;
        private String endYear;
        private String grade;
        private String description;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CertificationItem {
        private String name;
        private String issuer;
        private String issueDate;
        private String credentialUrl;
    }

}
