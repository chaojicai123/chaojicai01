package com.eldercare.repository;

import com.eldercare.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(
            String targetType, Long targetId, Pageable pageable);

    long countByTargetTypeAndTargetIdAndReadFlagFalse(String targetType, Long targetId);
}
