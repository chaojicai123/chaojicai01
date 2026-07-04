package com.eldercare.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentDTO {

    private Long id;
    private Long elderId;
    private String elderName;
    private Long staffId;
    private String staffName;
    private String serviceType;
    private LocalDateTime scheduledAt;
    private LocalDateTime actualStartAt;
    private LocalDateTime actualEndAt;
    private String status;
    private String note;
    private Instant createdAt;
}
