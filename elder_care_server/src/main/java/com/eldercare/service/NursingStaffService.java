package com.eldercare.service;

import com.eldercare.dto.NursingStaffDTO;
import com.eldercare.dto.PageResponse;
import com.eldercare.entity.NursingStaff;
import com.eldercare.repository.NursingStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NursingStaffService {

    private final NursingStaffRepository staffRepository;

    public PageResponse<NursingStaffDTO> list(String name, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<NursingStaff> p = name != null && !name.isBlank()
                ? staffRepository.findByNameContaining(name, pageable)
                : staffRepository.findAll(pageable);
        return PageResponse.<NursingStaffDTO>builder()
                .content(p.getContent().stream().map(this::toDTO).toList())
                .page(p.getNumber())
                .size(p.getSize())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .build();
    }

    /** 用于预约分配等场景，返回全部护理人员供选择 */
    public List<NursingStaffDTO> listAvailable() {
        return staffRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public NursingStaffDTO getById(Long id) {
        NursingStaff s = staffRepository.findById(id).orElseThrow(() -> new RuntimeException("护理人员不存在"));
        return toDTO(s);
    }

    @Transactional
    public NursingStaffDTO create(NursingStaff staff) {
        staff.setId(null);
        return toDTO(staffRepository.save(staff));
    }

    @Transactional
    public NursingStaffDTO update(Long id, NursingStaff staff) {
        NursingStaff existing = staffRepository.findById(id).orElseThrow(() -> new RuntimeException("护理人员不存在"));
        existing.setName(staff.getName());
        existing.setGender(staff.getGender());
        existing.setPhone(staff.getPhone());
        existing.setTitle(staff.getTitle());
        existing.setSkills(staff.getSkills());
        if (staff.getAvailable() != null) existing.setAvailable(staff.getAvailable());
        return toDTO(staffRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!staffRepository.existsById(id)) throw new RuntimeException("护理人员不存在");
        staffRepository.deleteById(id);
    }

    private NursingStaffDTO toDTO(NursingStaff s) {
        return NursingStaffDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .gender(s.getGender())
                .phone(s.getPhone())
                .title(s.getTitle())
                .skills(s.getSkills())
                .available(s.getAvailable())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
