package com.linkhub.mapper;

import com.linkhub.dto.SavedPostDto.SavedPostResponse;
import com.linkhub.entity.SavedPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SavedPostMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "post.content", target = "content")
    @Mapping(source = "post.imageUrl", target = "imageUrl")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "createdAt", target = "savedAt")
    SavedPostResponse toResponse(SavedPost savedPost);
}