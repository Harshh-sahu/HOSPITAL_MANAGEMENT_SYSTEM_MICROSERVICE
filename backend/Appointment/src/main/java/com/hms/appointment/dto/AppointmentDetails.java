package com.hms.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "AppointmentDetails", description = "Appointment details enriched with doctor and patient profile data")
public class AppointmentDetails {

    @Schema(description = "Appointment id", example = "1001")
    private Long id;
    @Schema(description = "Patient id", example = "2001")
    private Long patientId;
    @Schema(description = "Patient full name", example = "Aman Sharma")
    private String patientName;
    @Schema(description = "Patient email", example = "aman@example.com")
    private String patientEmail;
    @Schema(description = "Patient phone", example = "9876543210")
    private String patientPhone;

    @Schema(description = "Doctor id", example = "3001")
    private Long doctorId;

    @Schema(description = "Doctor full name", example = "Dr. Priya Mehta")
    private String doctorName;
    @Schema(description = "Appointment date-time", example = "2026-03-31T10:30:00")
    private LocalDateTime appointmentTime;
    @Schema(description = "Appointment status", implementation = Status.class)
    private Status status;
    @Schema(description = "Consultation reason", example = "Fever and headache")
    private String reason;
    @Schema(description = "Additional notes", example = "Patient reported symptoms for 2 days")
    private String notes;
}
