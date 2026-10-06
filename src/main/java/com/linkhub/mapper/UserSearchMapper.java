package com.linkhub.mapper;

import com.linkhub.dto.SearchDto.UserSearchResponse;
import com.linkhub.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserSearchMapper {

    @Mapping(source = "profile.profileType", target = "profileType")
    UserSearchResponse toResponse(User user);
}
