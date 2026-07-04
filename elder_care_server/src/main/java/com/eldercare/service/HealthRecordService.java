package com.eldercare.service;

import com.eldercare.dto.HealthRecordDTO;
import com.eldercare.dto.PageResponse;
import com.eldercare.entity.Elder;
import com.eldercare.entity.HealthRecord;
import com.eldercare.entity.User;
import com.eldercare.repository.ElderRepository;
import com.eldercare.repository.HealthRecordRepository;
import com.eldercare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final ElderRepository elderRepository;
    private final UserRepository userRepository;

    private Long getLinkedElderIdForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long)) return null;
        Long userId = (Long) auth.getPrincipal();
        return userRepository.findById(userId)
                .filter(u -> u.getRole() != User.Role.ADMIN && u.getLinkedElderId() != null)
                .map(User::getLinkedElderId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public PageResponse<HealthRecordDTO> listByElder(Long elderId, int page, int size) {
        Long linked = getLinkedElderIdForCurrentUser();
        if (linked != null && !linked.equals(elderId)) {
            return PageResponse.<HealthRecordDTO>builder()
                    .content(Collections.emptyList())
                    .page(page)
                    .size(size)
                    .totalElements(0)
                    .totalPages(0)
                    .build();
        }
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<HealthRecord> p = healthRecordRepository.findByElderIdOrderByRecordDateDesc(elderId, pageable);
        return PageResponse.<HealthRecordDTO>builder()
                .content(p.getContent().stream().map(this::toDTO).toList())
                .page(p.getNumber())
                .size(p.getSize())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public List<HealthRecordDTO> listByElderAndDateRange(Long elderId, LocalDate start, LocalDate end) {
        Long linked = getLinkedElderIdForCurrentUser();
        if (linked != null && !linked.equals(elderId)) return Collections.emptyList();
        return healthRecordRepository.findByElderIdAndRecordDateBetweenOrderByRecordDateDesc(elderId, start, end)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /** 管理员查看所有老人的健康数据 */
    @Transactional(readOnly = true)
    public PageResponse<HealthRecordDTO> listAll(int page, int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long)) {
            throw new RuntimeException("未登录");
        }
        
        Long userId = (Long) auth.getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 只有管理员可以查看所有数据
        if (user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("仅管理员可查看所有健康数据");
        }
        
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<HealthRecord> p = healthRecordRepository.findAllByOrderByRecordDateDesc(pageable);
        return PageResponse.<HealthRecordDTO>builder()
                .content(p.getContent().stream().map(this::toDTO).toList())
                .page(p.getNumber())
                .size(p.getSize())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public HealthRecordDTO getById(Long id) {
        HealthRecord r = healthRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("健康记录不存在"));
        Long linked = getLinkedElderIdForCurrentUser();
        if (linked != null && !linked.equals(r.getElder().getId())) throw new RuntimeException("无权限查看该健康记录");
        return toDTO(r);
    }

    @Transactional
    public HealthRecordDTO create(HealthRecord record) {
        Long elderId = record.getElder().getId();
        Long linked = getLinkedElderIdForCurrentUser();
        if (linked != null && !linked.equals(elderId)) throw new RuntimeException("仅可为关联老人录入健康数据");
        Elder elder = elderRepository.findById(elderId)
                .orElseThrow(() -> new RuntimeException("老人不存在"));
        record.setElder(elder);
        record.setId(null);
        return toDTO(healthRecordRepository.save(record));
    }

    @Transactional
    public void delete(Long id) {
        HealthRecord r = healthRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("健康记录不存在"));
        Long linked = getLinkedElderIdForCurrentUser();
        if (linked != null && !linked.equals(r.getElder().getId())) throw new RuntimeException("无权限删除该健康记录");
        healthRecordRepository.deleteById(id);
    }

    private HealthRecordDTO toDTO(HealthRecord r) {
        return HealthRecordDTO.builder()
                .id(r.getId())
                .elderId(r.getElder().getId())
                .elderName(r.getElder().getName())
                .recordDate(r.getRecordDate())
                .bloodPressureHigh(r.getBloodPressureHigh())
                .bloodPressureLow(r.getBloodPressureLow())
                .heartRate(r.getHeartRate())
                .bloodSugar(r.getBloodSugar())
                .temperature(r.getTemperature())
                .weightKg(r.getWeightKg())
                .remark(r.getRemark())
                .source(r.getSource())
                .createdAt(r.getCreatedAt())
                .build();
    }
}