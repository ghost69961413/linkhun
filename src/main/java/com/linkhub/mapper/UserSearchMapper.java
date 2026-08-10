package com.linkhub.mapper;

import com.linkhub.dto.SearchDto.UserSearchResponse;
import com.linkhub.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSearchMapper {

    UserSearchResponse toResponse(User user);
}