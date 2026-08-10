package com.linkhub.repository;

import com.linkhub.entity.Notification;
import com.linkhub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientOrderByCreatedAtDesc(
            User recipient,
            Pageable pageable
    );

    long countByRecipientAndIsReadFalse(User recipient);
}