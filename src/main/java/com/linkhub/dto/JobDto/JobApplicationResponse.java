package com.linkhub.dto.JobDto;

import com.linkhub.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplicationResponse {

    private Long id;

    private Long jobId;

    private String jobTitle;

    private Long applicantId;

    private String applicantName;

    private String coverLetter;

    private String resumeUrl;

    private ApplicationStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}