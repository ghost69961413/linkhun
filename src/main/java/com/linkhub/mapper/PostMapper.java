package com.linkhub.mapper;

import com.linkhub.dto.PostDto.PostResponse;
import com.linkhub.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(target = "fullName", expression = "java(post.getUser() == null ? null : (post.getUser().getFirstName() + \" \" + post.getUser().getLastName()).trim())")
    PostResponse toResponse(Post post);
}
