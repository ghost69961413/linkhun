package com.linkhub.dto.profile;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {

    private String headline;

    private String bio;

    private String location;

    private String website;

    private String githubUrl;

    private String linkedinUrl;

    private String portfolioUrl;

    private String company;

    private String designation;

}