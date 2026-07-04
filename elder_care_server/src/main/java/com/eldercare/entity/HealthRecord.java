package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "health_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elder_id", nullable = false)
    private Elder elder;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "blood_pressure_high", precision = 5, scale = 2)
    private BigDecimal bloodPressureHigh;

    @Column(name = "blood_pressure_low", precision = 5, scale = 2)
    private BigDecimal bloodPressureLow;

    @Column(name = "heart_rate", precision = 5, scale = 2)
    private BigDecimal heartRate;

    @Column(name = "blood_sugar", precision = 5, scale = 2)
    private BigDecimal bloodSugar;

    @Column(name = "temperature", precision = 4, scale = 2)
    private BigDecimal temperature;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
