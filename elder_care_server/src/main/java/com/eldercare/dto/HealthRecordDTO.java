package com.eldercare.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class HealthRecordDTO {

    private Long id;
    private Long elderId;
    private String elderName;
    private LocalDate recordDate;
    private BigDecimal bloodPressureHigh;
    private BigDecimal bloodPressureLow;
    private BigDecimal heartRate;
    private BigDecimal bloodSugar;
    private BigDecimal temperature;
    private BigDecimal weightKg;
    private String remark;
    private String source;
    private Instant createdAt;
}
