package com.eldercare.repository;

import com.eldercare.entity.Elder;
import com.eldercare.entity.Elder.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElderRepository extends JpaRepository<Elder, Long> {

    Page<Elder> findByNameContaining(String name, Pageable pageable);

    Page<Elder> findByStatus(Status status, Pageable pageable);
}
