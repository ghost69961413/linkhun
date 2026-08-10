package com.linkhub.service;

import com.linkhub.dto.NotificationDto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    Page<NotificationResponse> getMyNotifications(Pageable pageable);

    long getUnreadCount();

    void markAsRead(Long notificationId);

    void markAllAsRead();

    void deleteNotification(Long notificationId);

    NotificationResponse createNotification(
            Long recipientId,
            Long senderId,
            String type,
            String message,
            Long referenceId
    );
}