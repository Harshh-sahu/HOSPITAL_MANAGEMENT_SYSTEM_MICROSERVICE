package com.hms.appointment.dto;

import com.hms.appointment.entity.ApRecord;
import com.hms.appointment.entity.Appointment;
import com.hms.appointment.utility.StringListConverter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ApRecordDTO", description = "Appointment clinical report with optional prescription")
public class ApRecordDTO {
    @Schema(description = "Appointment record id", example = "5001")
    private Long id;

    @Schema(description = "Patient id", example = "2001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "patientId is required")
    private Long patientId;
    @Schema(description = "Doctor id", example = "3001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "doctorId is required")
    private Long doctorId;


    @Schema(description = "Appointment id", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "appointmentId is required")
    private Long appointmentId;

    @ArraySchema(schema = @Schema(example = "fever"), arraySchema = @Schema(description = "Symptoms observed"))
    private List<String> symptoms;
    @Schema(description = "Diagnosis text", example = "Viral infection")
    @Size(max = 500, message = "diagnosis must be at most 500 characters")
    private String diagnosis;
    @ArraySchema(schema = @Schema(example = "CBC"), arraySchema = @Schema(description = "Tests advised"))
    private List<String> tests;
    @Schema(description = "Clinical notes", example = "Hydration advised")
    @Size(max = 1000, message = "notes must be at most 1000 characters")
    private String notes;
    @Schema(description = "Referral details", example = "None")
    @Size(max = 500, message = "referral must be at most 500 characters")
    private String referral;
    @Schema(description = "Prescription linked to report")
    @Valid
    private PrescriptionDTO prescription;
    @Schema(description = "Follow-up date", example = "2026-04-05")
    private LocalDate followUpDate;
    @Schema(description = "Record creation timestamp", example = "2026-03-30T15:30:00")
    private LocalDateTime createdAt;


    public ApRecord toEntity(){
        return new ApRecord(
            this.id,
            this.patientId,
            this.doctorId,
            new Appointment(appointmentId),
                StringListConverter.convertListToString(this.symptoms),
            this.diagnosis,
             StringListConverter.convertListToString(this.tests),
            this.notes,
            this.referral,
            this.followUpDate,
            this.createdAt
        );
    }



}
