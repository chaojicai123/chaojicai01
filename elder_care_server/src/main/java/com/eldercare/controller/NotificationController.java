package com.eldercare.controller;

import com.eldercare.dto.NotificationDTO;
import com.eldercare.dto.PageResponse;
import com.eldercare.entity.Notification;
import com.eldercare.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<PageResponse<NotificationDTO>> list(
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(notificationService.list(targetType, targetId, page, size));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        return ResponseEntity.ok(notificationService.unreadCount(targetType, targetId));
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> create(@RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.create(notification));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ResponseEntity.ok().build();
    }
}
