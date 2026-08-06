package com.linkhub.dto.education;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationResponse {

    private Long id;

    private String collegeName;

    private String degree;

    private String fieldOfStudy;

    private Integer startYear;

    private Integer endYear;

    private Double grade;

    private String description;
}