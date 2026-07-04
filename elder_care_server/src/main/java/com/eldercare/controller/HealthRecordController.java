package com.eldercare.controller;

import com.eldercare.dto.HealthRecordDTO;
import com.eldercare.dto.HealthRecordCreateDTO;
import com.eldercare.dto.PageResponse;
import com.eldercare.entity.HealthRecord;
import com.eldercare.service.HealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    @GetMapping("/elder/{elderId}")
    public ResponseEntity<PageResponse<HealthRecordDTO>> listByElder(
            @PathVariable Long elderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(healthRecordService.listByElder(elderId, page, size));
    }

    @GetMapping("/elder/{elderId}/range")
    public ResponseEntity<List<HealthRecordDTO>> listByDateRange(
            @PathVariable Long elderId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(healthRecordService.listByElderAndDateRange(elderId, start, end));
    }

    /** 管理员查看所有老人的健康数据 */
    @GetMapping("/all")
    public ResponseEntity<PageResponse<HealthRecordDTO>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(healthRecordService.listAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthRecordDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(healthRecordService.getById(id));
    }

    @PostMapping
    public ResponseEntity<HealthRecordDTO> create(@RequestBody Map<String, Object> request) {
        try {
            Long elderId = Long.valueOf(request.get("elderId").toString());
            LocalDate recordDate = LocalDate.parse(request.get("recordDate").toString());
            
            HealthRecord record = new HealthRecord();
            record.setElder(new com.eldercare.entity.Elder());
            record.getElder().setId(elderId);
            record.setRecordDate(recordDate);
            
            // 处理数值类型，避免空值和类型转换问题
            if (request.get("bloodPressureHigh") != null && !"null".equals(request.get("bloodPressureHigh"))) {
                record.setBloodPressureHigh(new java.math.BigDecimal(request.get("bloodPressureHigh").toString()));
            }
            if (request.get("bloodPressureLow") != null && !"null".equals(request.get("bloodPressureLow"))) {
                record.setBloodPressureLow(new java.math.BigDecimal(request.get("bloodPressureLow").toString()));
            }
            if (request.get("heartRate") != null && !"null".equals(request.get("heartRate"))) {
                record.setHeartRate(new java.math.BigDecimal(request.get("heartRate").toString()));
            }
            if (request.get("bloodSugar") != null && !"null".equals(request.get("bloodSugar"))) {
                record.setBloodSugar(new java.math.BigDecimal(request.get("bloodSugar").toString()));
            }
            if (request.get("temperature") != null && !"null".equals(request.get("temperature"))) {
                record.setTemperature(new java.math.BigDecimal(request.get("temperature").toString()));
            }
            if (request.get("weightKg") != null && !"null".equals(request.get("weightKg"))) {
                record.setWeightKg(new java.math.BigDecimal(request.get("weightKg").toString()));
            }
            
            if (request.get("source") != null) record.setSource(request.get("source").toString());
            if (request.get("remark") != null) record.setRemark(request.get("remark").toString());
            
            return ResponseEntity.ok(healthRecordService.create(record));
        } catch (Exception e) {
            throw new RuntimeException("录入失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        healthRecordService.delete(id);
        return ResponseEntity.ok().build();
    }
}