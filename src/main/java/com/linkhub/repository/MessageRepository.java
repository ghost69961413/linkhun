package com.linkhub.repository;

import com.linkhub.entity.Chat;
import com.linkhub.entity.Message;
import com.linkhub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByChat(
            Chat chat,
            Pageable pageable
    );

    long countByChatAndReadFalse(Chat chat);

    long countByChatAndSenderNotAndReadFalse(Chat chat, User sender);

    java.util.Optional<Message> findTopByChatOrderByCreatedAtDesc(Chat chat);
}
