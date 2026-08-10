package com.linkhub.mapper;

import com.linkhub.dto.NotificationDto.NotificationResponse;
import com.linkhub.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "sender.username", target = "senderUsername")
    NotificationResponse toResponse(Notification notification);
}