package com.hms.appointment.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "RecordDetails", description = "Enriched appointment record details")
public class RecordDetails {
    @Schema(description = "Record id", example = "5001")
    private Long id;

    @Schema(description = "Patient id", example = "2001")
    private Long patientId;
    @Schema(description = "Doctor id", example = "3001")
    private Long doctorId;
    @Schema(description = "Doctor name", example = "Dr. Priya Mehta")
    private String doctorName;

    @Schema(description = "Appointment id", example = "1001")
    private Long appointmentId;

    @ArraySchema(schema = @Schema(example = "fever"), arraySchema = @Schema(description = "Symptoms list"))
    private List<String> symptoms;
    @Schema(description = "Diagnosis", example = "Viral infection")
    private String diagnosis;
    @ArraySchema(schema = @Schema(example = "CBC"), arraySchema = @Schema(description = "Tests list"))
    private List<String> tests;
    @Schema(description = "Clinical notes", example = "Hydration advised")
    private String notes;
    @Schema(description = "Referral details", example = "None")
    private String referral;
    @Schema(description = "Follow-up date", example = "2026-04-05")
    private LocalDate followUpDate;
    @Schema(description = "Record created timestamp", example = "2026-03-30T15:30:00")
    private LocalDateTime createdAt;


}
