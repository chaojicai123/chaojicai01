package com.eldercare.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HealthRecordCreateDTO {
    private Long elderId;
    private LocalDate recordDate;
    private Double bloodPressureHigh;
    private Double bloodPressureLow;
    private Double heartRate;
    private Double bloodSugar;
    private Double temperature;
    private Double weightKg;
    private String remark;
    private String source;
}