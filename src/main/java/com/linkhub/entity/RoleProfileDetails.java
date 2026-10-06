package com.linkhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profile_role_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleProfileDetails extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    @Column(name = "details_json", nullable = false, columnDefinition = "LONGTEXT")
    private String detailsJson;
}
