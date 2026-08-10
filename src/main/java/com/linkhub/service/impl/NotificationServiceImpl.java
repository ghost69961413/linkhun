package com.linkhub.service.impl;

import com.linkhub.dto.NotificationDto.NotificationResponse;
import com.linkhub.entity.Notification;
import com.linkhub.entity.User;
import com.linkhub.exception.BadRequestException;
import com.linkhub.exception.UserNotFoundException;
import com.linkhub.mapper.NotificationMapper;
import com.linkhub.repository.NotificationRepository;
import com.linkhub.repository.UserRepository;
import com.linkhub.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(
            Pageable pageable) {

        User user = getCurrentUser();

        return notificationRepository
                .findByRecipientOrderByCreatedAtDesc(user, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {

        User user = getCurrentUser();

        return notificationRepository
                .countByRecipientAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {

        User user = getCurrentUser();

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Notification not found"));

        if (!notification.getRecipient()
                .getId()
                .equals(user.getId())) {

            throw new BadRequestException(
                    "You can only update your own notifications");
        }

        notification.setIsRead(true);

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {

        User user = getCurrentUser();

        Page<Notification> notifications =
                notificationRepository
                        .findByRecipientOrderByCreatedAtDesc(
                                user,
                                Pageable.unpaged()
                        );

        notifications.forEach(notification ->
                notification.setIsRead(true));

        notificationRepository.saveAll(
                notifications.getContent()
        );
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {

        User user = getCurrentUser();

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Notification not found"));

        if (!notification.getRecipient()
                .getId()
                .equals(user.getId())) {

            throw new BadRequestException(
                    "You can only delete your own notifications");
        }

        notificationRepository.delete(notification);
    }
    @Override
    @Transactional
    public NotificationResponse createNotification(
            Long recipientId,
            Long senderId,
            String type,
            String message,
            Long referenceId) {

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() ->
                        new UserNotFoundException("Recipient user not found"));

        User sender = null;

        if (senderId != null) {
            sender = userRepository.findById(senderId)
                    .orElseThrow(() ->
                            new UserNotFoundException("Sender user not found"));
        }

        Notification notification = Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(type)
                .message(message)
                .referenceId(referenceId)
                .isRead(false)
                .build();

        Notification savedNotification =
                notificationRepository.save(notification);

        return notificationMapper.toResponse(savedNotification);
    }
}