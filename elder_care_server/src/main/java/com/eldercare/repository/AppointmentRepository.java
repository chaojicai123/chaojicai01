package com.eldercare.repository;

import com.eldercare.entity.Appointment;
import com.eldercare.entity.Appointment.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Page<Appointment> findByElderId(Long elderId, Pageable pageable);

    Page<Appointment> findByStaffId(Long staffId, Pageable pageable);

    Page<Appointment> findByStatus(Status status, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.scheduledAt BETWEEN :start AND :end")
    List<Appointment> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);
}
