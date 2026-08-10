package com.linkhub.mapper;

import com.linkhub.dto.PostDto.PostResponse;
import com.linkhub.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostResponse toResponse(Post post);
}