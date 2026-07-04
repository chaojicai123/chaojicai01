package com.eldercare.service;

import com.eldercare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ElderRepository elderRepository;
    private final NursingStaffRepository nursingStaffRepository;
    private final AppointmentRepository appointmentRepository;
    private final HealthRecordRepository healthRecordRepository;

    public Map<String, Object> dashboard() {
        Map<String, Object> map = new HashMap<>();
        map.put("elderCount", elderRepository.count());
        map.put("staffCount", nursingStaffRepository.count());
        map.put("appointmentCount", appointmentRepository.count());
        map.put("healthRecordCount", healthRecordRepository.count());
        return map;
    }
}
