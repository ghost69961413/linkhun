package com.linkhub.dto.JobDto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplicationRequest {

    @NotBlank(message = "Cover letter is required")
    private String coverLetter;

    private String resumeUrl;
}