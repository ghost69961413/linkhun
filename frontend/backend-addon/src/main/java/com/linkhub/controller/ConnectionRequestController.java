package com.linkhub.controller;

import com.linkhub.dto.connection.ConnectionRequestDto;
import com.linkhub.dto.connection.ConnectionUserDto;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.ConnectionRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
public class ConnectionRequestController {
    private final ConnectionRequestService connectionService;

    @PostMapping("/request/{userId}")
    public ResponseEntity<ApiResponse<ConnectionRequestDto>> send(@PathVariable Long userId) {
        return response(HttpStatus.CREATED, "Connection request sent", connectionService.sendRequest(userId));
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<ApiResponse<ConnectionRequestDto>> accept(@PathVariable Long requestId) {
        return response(HttpStatus.OK, "Connection request accepted", connectionService.acceptRequest(requestId));
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<ApiResponse<ConnectionRequestDto>> reject(@PathVariable Long requestId) {
        return response(HttpStatus.OK, "Connection request rejected", connectionService.rejectRequest(requestId));
    }

    @DeleteMapping("/withdraw/{requestId}")
    public ResponseEntity<ApiResponse<Void>> withdraw(@PathVariable Long requestId) {
        connectionService.withdrawRequest(requestId);
        return response(HttpStatus.OK, "Connection request withdrawn", null);
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<ConnectionRequestDto>>> pending() {
        return response(HttpStatus.OK, "Pending requests fetched", connectionService.getPendingRequests());
    }

    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<List<ConnectionRequestDto>>> sent() {
        return response(HttpStatus.OK, "Sent requests fetched", connectionService.getSentRequests());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConnectionRequestDto>>> connections() {
        return response(HttpStatus.OK, "Connections fetched", connectionService.getConnections());
    }

    @GetMapping("/mutual/{userId}")
    public ResponseEntity<ApiResponse<List<ConnectionUserDto>>> mutual(@PathVariable Long userId) {
        return response(HttpStatus.OK, "Mutual connections fetched", connectionService.getMutualConnections(userId));
    }

    private <T> ResponseEntity<ApiResponse<T>> response(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(ApiResponse.<T>builder().success(true).message(message).data(data).build());
    }
}
