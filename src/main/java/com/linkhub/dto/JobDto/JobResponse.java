package com.linkhub.dto.JobDto;

import com.linkhub.enums.JobType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {

    private Long id;

    private String title;

    private String description;

    private String companyName;

    private String location;

    private String salary;

    private JobType jobType;

    private String skills;

    private Boolean active;

    private Long postedById;

    private String postedByName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}