package com.hms.appointment.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PrescriptionDetails", description = "Prescription data enriched with doctor and patient names")
public class PrescriptionDetails {
    @Schema(description = "Prescription id", example = "7001")
    private Long id;

    @Schema(description = "Patient id", example = "2001")
    private Long patientId;
    @Schema(description = "Doctor id", example = "3001")
    private Long doctorId;
    @Schema(description = "Doctor name", example = "Dr. Priya Mehta")
    private String doctorName;

    @Schema(description = "Patient name", example = "Aman Sharma")
    private String patientName;
    @Schema(description = "Appointment id", example = "1001")
    private Long appointmentId;

    @Schema(description = "Prescription date", example = "2026-03-30")
    private LocalDate prescriptionDate;
    @Schema(description = "Prescription notes", example = "Take medicines after food")
    private String notes;
    @ArraySchema(schema = @Schema(implementation = MedicineDTO.class), arraySchema = @Schema(description = "Medicines in prescription"))
    private List<MedicineDTO> medicines;

}
