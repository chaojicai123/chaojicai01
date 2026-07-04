package com.eldercare.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NotificationDTO {

    private Long id;
    private String targetType;
    private Long targetId;
    private String title;
    private String content;
    private String type;
    private Boolean readFlag;
    private Instant createdAt;
}
