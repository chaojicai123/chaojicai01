package com.eldercare.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NursingStaffDTO {

    private Long id;
    private String name;
    private String gender;
    private String phone;
    private String title;
    private String skills;
    private Boolean available;
    private Instant createdAt;
}
