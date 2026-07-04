package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "elders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Elder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 2)
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 50)
    private String idCard;

    @Column(length = 200)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 50)
    private String emergencyContact;

    @Column(length = 20)
    private String emergencyPhone;

    @Column(columnDefinition = "TEXT")
    private String healthCondition;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    @OneToMany(mappedBy = "elder", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private java.util.List<com.eldercare.entity.HealthRecord> healthRecords;

    public enum Status { ACTIVE, INACTIVE, DECEASED }
}
