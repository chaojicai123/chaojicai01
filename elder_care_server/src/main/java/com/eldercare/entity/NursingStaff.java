package com.eldercare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "nursing_staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NursingStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 2)
    private String gender;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
