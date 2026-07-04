package com.eldercare.controller;

import com.eldercare.dto.AppointmentDTO;
import com.eldercare.dto.PageResponse;
import com.eldercare.entity.Appointment;
import com.eldercare.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<PageResponse<AppointmentDTO>> list(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) Long staffId,
            @RequestParam(required = false) Appointment.Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(appointmentService.list(elderId, staffId, status, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> create(@RequestBody Appointment appointment) {
        return ResponseEntity.ok(appointmentService.create(appointment));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentDTO> updateStatus(@PathVariable Long id, @RequestParam Appointment.Status status) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<AppointmentDTO> assignStaff(@PathVariable Long id, @RequestParam Long staffId) {
        return ResponseEntity.ok(appointmentService.assignStaff(id, staffId));
    }
}
