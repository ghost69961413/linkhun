package com.linkhub.dto.experience;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceRequest {

    @NotBlank
    private String companyName;

    @NotBlank
    private String designation;

    @NotBlank
    private String employmentType;

    @NotBlank
    private String location;

    @NotNull
    private Integer startYear;

    private Integer endYear;

    private Boolean currentlyWorking;

    private String description;
}