package com.linkhub.mapper;

import com.linkhub.dto.SearchDto.PostSearchResponse;
import com.linkhub.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostSearchMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    PostSearchResponse toResponse(Post post);
}