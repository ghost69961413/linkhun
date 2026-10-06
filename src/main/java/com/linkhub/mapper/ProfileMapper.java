package com.linkhub.mapper;

import com.linkhub.dto.profile.ProfileRequest;
import com.linkhub.dto.profile.ProfileResponse;
import com.linkhub.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    // Entity -> Response DTO
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "profilePictureUrl", target = "profilePicture")
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "experience", ignore = true)
    @Mapping(target = "education", ignore = true)
    @Mapping(target = "certifications", ignore = true)
    @Mapping(target = "coverImage", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "roleDetails", ignore = true)
    ProfileResponse toResponse(Profile profile);

    // Request DTO -> Entity
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "experiences", ignore = true)
    @Mapping(target = "educations", ignore = true)
    @Mapping(target = "certifications", ignore = true)
    @Mapping(target = "roleDetails", ignore = true)
    Profile toEntity(ProfileRequest request);

    // Update Existing Entity
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "experiences", ignore = true)
    @Mapping(target = "educations", ignore = true)
    @Mapping(target = "certifications", ignore = true)
    @Mapping(target = "roleDetails", ignore = true)
    void updateProfile(ProfileRequest request,
                       @MappingTarget Profile profile);
}
