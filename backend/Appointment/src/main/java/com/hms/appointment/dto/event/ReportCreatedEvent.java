package com.hms.appointment.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCreatedEvent {
    private Long recordId;
    private Long appointmentId;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private Long doctorId;
    private String doctorName;
    private List<String> symptoms;
    private String diagnosis;
    private List<String> tests;
    private String notes;
    private String referral;
    private LocalDate followUpDate;
    private LocalDateTime createdAt;
    private List<MedicineInfo> medicines;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicineInfo {
        private String name;
        private String dosage;
        private String frequency;
        private Integer duration;
        private String type;
        private String instructions;
    }
}
