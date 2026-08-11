package com.linkhub.repository;

import com.linkhub.entity.Chat;
import com.linkhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByParticipantsContaining(User user);
}