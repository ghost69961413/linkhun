CREATE TABLE connection_requests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_connection_sender_recipient UNIQUE (sender_id, recipient_id),
    CONSTRAINT fk_connection_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_connection_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_connection_not_self CHECK (sender_id <> recipient_id),
    CONSTRAINT chk_connection_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
    INDEX idx_connection_recipient_status (recipient_id, status),
    INDEX idx_connection_sender_status (sender_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
