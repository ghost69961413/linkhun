package com.linkhub.mapper;

import com.linkhub.dto.FollowDto.FollowResponse;
import com.linkhub.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FollowMapper {

    FollowResponse toResponse(User user);

}