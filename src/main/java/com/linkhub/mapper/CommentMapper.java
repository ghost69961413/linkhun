package com.linkhub.mapper;

import com.linkhub.dto.CommentDto.CommentResponse;
import com.linkhub.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    CommentResponse toResponse(Comment comment);
}