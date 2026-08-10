package com.linkhub.controller;

import com.linkhub.dto.NotificationDto.NotificationResponse;
import com.linkhub.response.ApiResponse;
import com.linkhub.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>>
    getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return ResponseEntity.ok(
                ApiResponse.<Page<NotificationResponse>>builder()
                        .success(true)
                        .message("Notifications fetched successfully")
                        .data(notificationService
                                .getMyNotifications(pageable))
                        .build()
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {

        return ResponseEntity.ok(
                ApiResponse.<Long>builder()
                        .success(true)
                        .message("Unread notification count fetched")
                        .data(notificationService.getUnreadCount())
                        .build()
        );
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId) {

        notificationService.markAsRead(notificationId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Notification marked as read")
                        .build()
        );
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {

        notificationService.markAllAsRead();

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("All notifications marked as read")
                        .build()
        );
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Long notificationId) {

        notificationService.deleteNotification(notificationId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Notification deleted successfully")
                        .build()
        );
    }
}