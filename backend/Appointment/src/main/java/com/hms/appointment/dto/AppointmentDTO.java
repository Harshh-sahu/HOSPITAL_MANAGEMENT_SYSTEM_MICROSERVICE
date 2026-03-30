package com.hms.appointment.dto;

import com.hms.appointment.entity.Appointment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "AppointmentDTO", description = "Appointment payload and details")
public class AppointmentDTO {
    @Schema(description = "Appointment id", example = "1001")
    private Long id;

    @Schema(description = "Patient id", example = "2001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "patientId is required")
    private Long patientId;

    @Schema(description = "Doctor id", example = "3001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "doctorId is required")
    private Long doctorId;

    @Schema(description = "Appointment date-time in ISO format", example = "2026-03-31T10:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "appointmentTime is required")
    private LocalDateTime appointmentTime;

    @Schema(description = "Appointment status", implementation = Status.class, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "status is required")
    private Status status;

    @Schema(description = "Reason for consultation", example = "Fever and headache")
    @Size(max = 255, message = "reason must be at most 255 characters")
    private String reason;

    @Schema(description = "Additional clinical notes", example = "Patient reported symptoms for 2 days")
    @Size(max = 1000, message = "notes must be at most 1000 characters")
    private String notes;


    public Appointment toEntity(){
        return new Appointment(
            this.id,
            this.patientId,
            this.doctorId,
            this.appointmentTime,
            this.status,
            this.reason,
            this.notes
        );

    }
}
