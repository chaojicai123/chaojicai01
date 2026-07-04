package com.eldercare.service;

import com.eldercare.dto.AppointmentDTO;
import com.eldercare.dto.PageResponse;
import com.eldercare.entity.Appointment;
import com.eldercare.entity.Elder;
import com.eldercare.entity.NursingStaff;
import com.eldercare.repository.AppointmentRepository;
import com.eldercare.repository.ElderRepository;
import com.eldercare.repository.NursingStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ElderRepository elderRepository;
    private final NursingStaffRepository staffRepository;

    @Transactional(readOnly = true)
    public PageResponse<AppointmentDTO> list(Long elderId, Long staffId, Appointment.Status status, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Appointment> p;
        if (elderId != null) p = appointmentRepository.findByElderId(elderId, pageable);
        else if (staffId != null) p = appointmentRepository.findByStaffId(staffId, pageable);
        else if (status != null) p = appointmentRepository.findByStatus(status, pageable);
        else p = appointmentRepository.findAll(pageable);
        return PageResponse.<AppointmentDTO>builder()
                .content(p.getContent().stream().map(this::toDTO).toList())
                .page(p.getNumber())
                .size(p.getSize())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public AppointmentDTO getById(Long id) {
        Appointment a = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("预约不存在"));
        return toDTO(a);
    }

    @Transactional
    public AppointmentDTO create(Appointment appointment) {
        Elder elder = elderRepository.findById(appointment.getElder().getId())
                .orElseThrow(() -> new RuntimeException("老人不存在"));
        appointment.setElder(elder);
        appointment.setId(null);
        appointment.setStatus(Appointment.Status.PENDING);
        if (appointment.getStaff() != null && appointment.getStaff().getId() != null) {
            NursingStaff staff = staffRepository.findById(appointment.getStaff().getId())
                    .orElseThrow(() -> new RuntimeException("护理人员不存在"));
            appointment.setStaff(staff);
        } else {
            appointment.setStaff(null);
        }
        return toDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDTO updateStatus(Long id, Appointment.Status status) {
        Appointment a = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("预约不存在"));
        a.setStatus(status);
        return toDTO(appointmentRepository.save(a));
    }

    @Transactional
    public AppointmentDTO assignStaff(Long id, Long staffId) {
        Appointment a = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("预约不存在"));
        NursingStaff staff = staffRepository.findById(staffId).orElseThrow(() -> new RuntimeException("护理人员不存在"));
        a.setStaff(staff);
        if (a.getStatus() == Appointment.Status.PENDING) a.setStatus(Appointment.Status.CONFIRMED);
        return toDTO(appointmentRepository.save(a));
    }

    private AppointmentDTO toDTO(Appointment a) {
        return AppointmentDTO.builder()
                .id(a.getId())
                .elderId(a.getElder().getId())
                .elderName(a.getElder().getName())
                .staffId(a.getStaff() != null ? a.getStaff().getId() : null)
                .staffName(a.getStaff() != null ? a.getStaff().getName() : null)
                .serviceType(a.getServiceType())
                .scheduledAt(a.getScheduledAt())
                .actualStartAt(a.getActualStartAt())
                .actualEndAt(a.getActualEndAt())
                .status(a.getStatus().name())
                .note(a.getNote())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
