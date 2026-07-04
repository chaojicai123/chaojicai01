package com.eldercare.repository;

import com.eldercare.entity.HealthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    Page<HealthRecord> findByElderIdOrderByRecordDateDesc(Long elderId, Pageable pageable);

    List<HealthRecord> findByElderIdAndRecordDateBetweenOrderByRecordDateDesc(
            Long elderId, LocalDate start, LocalDate end);
            
    /** 管理员查看所有健康记录，按日期倒序排列 */
    Page<HealthRecord> findAllByOrderByRecordDateDesc(Pageable pageable);
}