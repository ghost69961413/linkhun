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
    @Mapping(source = "user.profilePicture", target = "profilePicture")
    ProfileResponse toResponse(Profile profile);

    // Request DTO -> Entity
    Profile toEntity(ProfileRequest request);

    // Update Existing Entity
    void updateProfile(ProfileRequest request,
                       @MappingTarget Profile profile);
}