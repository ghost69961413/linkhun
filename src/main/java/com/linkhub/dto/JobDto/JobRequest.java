package com.linkhub.dto.JobDto;

import com.linkhub.enums.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String location;

    private String salary;

    @NotNull(message = "Job type is required")
    private JobType jobType;

    private String skills;
}