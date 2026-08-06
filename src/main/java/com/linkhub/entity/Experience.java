package com.linkhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "experience")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experience extends BaseEntity {

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String designation;

    @Column(nullable = false)
    private String employmentType;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Integer startYear;

    private Integer endYear;

    @Builder.Default
    private Boolean currentlyWorking = false;

    @Column(length = 1500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;
}