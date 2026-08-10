package com.linkhub.mapper;

import com.linkhub.dto.LikeDto.LikeResponse;
import com.linkhub.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "user.id", target = "userId")
    LikeResponse toResponse(Like like);
}