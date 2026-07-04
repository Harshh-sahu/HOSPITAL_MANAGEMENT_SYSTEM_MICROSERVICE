package com.hms.profile.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegisteredEvent {
    private Long patientId;
    private String name;
    private String email;
    private String phone;
    private String bloodGroup;
    private LocalDate registeredAt;
}
