package com.linkhub.entity;

import jakarta.persistence.*;
import com.linkhub.enums.ProfileType;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_type", nullable = false, length = 24)
    @Builder.Default
    private ProfileType profileType = ProfileType.PROFESSIONAL;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RoleProfileDetails roleDetails;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "profile_skills",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "cover_image_url", length = 1000)
    private String coverImageUrl;


    @Column(length = 255)
    private String headline;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String location;

    private String website;

    private String githubUrl;

    private String linkedinUrl;

    private String portfolioUrl;

    private String company;

    private String designation;

    private Integer followers = 0;

    private Integer following = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(
            mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Education> educations = new ArrayList<>();

    @OneToMany(
            mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(
            mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Certification> certifications = new ArrayList<>();
}
