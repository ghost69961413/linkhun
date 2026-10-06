package com.linkhub.service;

import com.linkhub.dto.connection.ConnectionRequestDto;
import com.linkhub.dto.connection.ConnectionUserDto;

import java.util.List;

public interface ConnectionRequestService {
    ConnectionRequestDto sendRequest(Long recipientId);
    ConnectionRequestDto acceptRequest(Long requestId);
    ConnectionRequestDto rejectRequest(Long requestId);
    void withdrawRequest(Long requestId);
    List<ConnectionRequestDto> getPendingRequests();
    List<ConnectionRequestDto> getSentRequests();
    List<ConnectionRequestDto> getConnections();
    List<ConnectionUserDto> getMutualConnections(Long userId);
}
