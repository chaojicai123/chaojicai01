package com.eldercare.service;

import com.eldercare.dto.NotificationDTO;
import com.eldercare.dto.PageResponse;
import com.eldercare.entity.Notification;
import com.eldercare.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public PageResponse<NotificationDTO> list(String targetType, Long targetId, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Notification> p = targetType != null && targetId != null
                ? notificationRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId, pageable)
                : notificationRepository.findAll(pageable);
        return PageResponse.<NotificationDTO>builder()
                .content(p.getContent().stream().map(this::toDTO).toList())
                .page(p.getNumber())
                .size(p.getSize())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .build();
    }

    public long unreadCount(String targetType, Long targetId) {
        return notificationRepository.countByTargetTypeAndTargetIdAndReadFlagFalse(targetType, targetId);
    }

    @Transactional
    public NotificationDTO create(Notification notification) {
        notification.setId(null);
        notification.setReadFlag(false);
        return toDTO(notificationRepository.save(notification));
    }

    @Transactional
    public void markRead(Long id) {
        Notification n = notificationRepository.findById(id).orElseThrow(() -> new RuntimeException("通知不存在"));
        n.setReadFlag(true);
        notificationRepository.save(n);
    }

    private NotificationDTO toDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .targetType(n.getTargetType())
                .targetId(n.getTargetId())
                .title(n.getTitle())
                .content(n.getContent())
                .type(n.getType())
                .readFlag(n.getReadFlag())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
