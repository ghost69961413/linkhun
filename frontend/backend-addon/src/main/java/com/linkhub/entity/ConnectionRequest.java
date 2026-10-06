package com.linkhub.entity;

import com.linkhub.enums.ConnectionStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "connection_requests",
       uniqueConstraints = @UniqueConstraint(name = "uk_connection_sender_recipient",
               columnNames = {"sender_id", "recipient_id"}),
       indexes = {
               @Index(name = "idx_connection_recipient_status", columnList = "recipient_id,status"),
               @Index(name = "idx_connection_sender_status", columnList = "sender_id,status")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionRequest extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false, updatable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false, updatable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private ConnectionStatus status = ConnectionStatus.PENDING;
}
