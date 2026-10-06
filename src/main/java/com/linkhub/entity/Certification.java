package com.linkhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profile_certifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Certification extends BaseEntity {
    @Column(nullable = false, length = 255)
    private String name;
    @Column(length = 255)
    private String issuer;
    @Column(length = 100)
    private String issueDate;
    @Column(length = 1000)
    private String credentialUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}
