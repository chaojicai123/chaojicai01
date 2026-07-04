package com.eldercare.repository;

import com.eldercare.entity.NursingStaff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NursingStaffRepository extends JpaRepository<NursingStaff, Long> {

    Page<NursingStaff> findByNameContaining(String name, Pageable pageable);

    List<NursingStaff> findByAvailableTrue();
}
