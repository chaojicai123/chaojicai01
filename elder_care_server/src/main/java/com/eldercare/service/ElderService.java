package com.eldercare.service;

import com.eldercare.dto.ElderDTO;
import com.eldercare.dto.PageResponse;
import com.eldercare.entity.Elder;
import com.eldercare.entity.Elder.Status;
import com.eldercare.entity.User;
import com.eldercare.repository.ElderRepository;
import com.eldercare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElderService {

    private final ElderRepository elderRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<ElderDTO> list(String name, Status status, int page, int size) {
        Long linkedElderId = getLinkedElderIdForCurrentUser();
        if (linkedElderId != null) {
            return listForLinkedElder(linkedElderId, page, size);
        }
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Elder> p = name != null && !name.isBlank()
                ? elderRepository.findByNameContaining(name, pageable)
                : status != null
                ? elderRepository.findByStatus(status, pageable)
                : elderRepository.findAll(pageable);
        return PageResponse.<ElderDTO>builder()
                .content(p.getContent().stream().map(this::toDTO).toList())
                .page(p.getNumber())
                .size(p.getSize())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .build();
    }

    /** 非管理员且已关联老人时返回其 linkedElderId，管理员或未关联时返回 null（表示查看全部） */
    private Long getLinkedElderIdForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long)) return null;
        Long userId = (Long) auth.getPrincipal();
        return userRepository.findById(userId)
                .filter(u -> u.getRole() != User.Role.ADMIN && u.getLinkedElderId() != null)
                .map(User::getLinkedElderId)
                .orElse(null);
    }

    private PageResponse<ElderDTO> listForLinkedElder(Long elderId, int page, int size) {
        if (page > 0) {
            return PageResponse.<ElderDTO>builder()
                    .content(Collections.emptyList())
                    .page(page)
                    .size(size)
                    .totalElements(1)
                    .totalPages(1)
                    .build();
        }
        List<ElderDTO> content = elderRepository.findById(elderId)
                .map(this::toDTO)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
        return PageResponse.<ElderDTO>builder()
                .content(content)
                .page(0)
                .size(size)
                .totalElements(content.size())
                .totalPages(content.isEmpty() ? 0 : 1)
                .build();
    }

    @Transactional(readOnly = true)
    public ElderDTO getById(Long id) {
        Long linked = getLinkedElderIdForCurrentUser();
        if (linked != null && !linked.equals(id)) {
            throw new RuntimeException("无权限查看该老人信息");
        }
        Elder e = elderRepository.findById(id).orElseThrow(() -> new RuntimeException("老人信息不存在"));
        return toDTO(e);
    }

    @Transactional
    public ElderDTO create(Elder elder) {
        elder.setId(null);
        return toDTO(elderRepository.save(elder));
    }

    @Transactional
    public ElderDTO update(Long id, Elder elder) {
        Elder existing = elderRepository.findById(id).orElseThrow(() -> new RuntimeException("老人信息不存在"));
        existing.setName(elder.getName());
        existing.setGender(elder.getGender());
        existing.setBirthDate(elder.getBirthDate());
        existing.setIdCard(elder.getIdCard());
        existing.setAddress(elder.getAddress());
        existing.setPhone(elder.getPhone());
        existing.setEmergencyContact(elder.getEmergencyContact());
        existing.setEmergencyPhone(elder.getEmergencyPhone());
        existing.setHealthCondition(elder.getHealthCondition());
        existing.setRemark(elder.getRemark());
        if (elder.getStatus() != null) existing.setStatus(elder.getStatus());
        return toDTO(elderRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        Elder existing = elderRepository.findById(id).orElseThrow(() -> new RuntimeException("老人信息不存在"));
        elderRepository.delete(existing);
    }

    private ElderDTO toDTO(Elder e) {
        return ElderDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .gender(e.getGender())
                .birthDate(e.getBirthDate())
                .idCard(e.getIdCard())
                .address(e.getAddress())
                .phone(e.getPhone())
                .emergencyContact(e.getEmergencyContact())
                .emergencyPhone(e.getEmergencyPhone())
                .healthCondition(e.getHealthCondition())
                .remark(e.getRemark())
                .status(e.getStatus() != null ? e.getStatus().name() : null)
                .createdAt(e.getCreatedAt())
                .build();
    }
}
