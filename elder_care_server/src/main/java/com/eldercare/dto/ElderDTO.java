package com.eldercare.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.Instant;

@Data
@Builder
public class ElderDTO {

    private Long id;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String idCard;
    private String address;
    private String phone;
    private String emergencyContact;
    private String emergencyPhone;
    private String healthCondition;
    private String remark;
    private String status;
    private Instant createdAt;
}
