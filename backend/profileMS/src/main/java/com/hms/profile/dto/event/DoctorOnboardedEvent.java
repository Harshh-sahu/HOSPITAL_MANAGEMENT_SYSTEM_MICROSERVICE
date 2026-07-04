package com.hms.profile.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorOnboardedEvent {
    private Long doctorId;
    private String name;
    private String email;
    private String licenseNo;
    private String specialization;
    private String department;
    private Integer totalExp;
    private String phone;
    private LocalDate onboardedAt;
}
