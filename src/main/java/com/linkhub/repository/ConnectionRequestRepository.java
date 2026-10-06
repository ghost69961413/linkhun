package com.linkhub.repository;

import com.linkhub.entity.ConnectionRequest;
import com.linkhub.entity.User;
import com.linkhub.enums.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {
    Optional<ConnectionRequest> findBySenderAndRecipient(User sender, User recipient);

    Optional<ConnectionRequest> findByIdAndRecipientAndStatus(Long id, User recipient, ConnectionStatus status);

    Optional<ConnectionRequest> findByIdAndSenderAndStatus(Long id, User sender, ConnectionStatus status);

    boolean existsBySenderAndRecipientAndStatus(User sender, User recipient, ConnectionStatus status);

    List<ConnectionRequest> findByRecipientAndStatusOrderByCreatedAtDesc(User recipient, ConnectionStatus status);

    List<ConnectionRequest> findBySenderAndStatusOrderByCreatedAtDesc(User sender, ConnectionStatus status);

    @Query("select cr from ConnectionRequest cr where cr.status = :status and (cr.sender = :user or cr.recipient = :user) order by cr.updatedAt desc")
    List<ConnectionRequest> findConnections(@Param("user") User user, @Param("status") ConnectionStatus status);

    @Query("select case when cr.sender = :user then cr.recipient.id else cr.sender.id end from ConnectionRequest cr where cr.status = :status and (cr.sender = :user or cr.recipient = :user)")
    List<Long> findConnectedUserIds(@Param("user") User user, @Param("status") ConnectionStatus status);
}
