package com.linkhub.dto.experience;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceResponse {

    private Long id;

    private String companyName;

    private String designation;

    private String employmentType;

    private String location;

    private Integer startYear;

    private Integer endYear;

    private Boolean currentlyWorking;

    private String description;
}