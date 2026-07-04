package com.hms.appointment.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionCreatedEvent {
    private Long prescriptionId;
    private Long appointmentId;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private Long doctorId;
    private String doctorName;
    private LocalDate prescriptionDate;
    private String notes;
    private List<String> medicines;
    private List<MedicineInfo> medicineDetails;

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
