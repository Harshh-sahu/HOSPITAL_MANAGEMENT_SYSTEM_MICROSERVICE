package com.hms.appointment.dto;

import com.hms.appointment.entity.Appointment;
import com.hms.appointment.entity.Prescription;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PrescriptionDTO", description = "Prescription details and medicine list")
public class PrescriptionDTO {

    @Schema(description = "Prescription id", example = "7001")
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

    @Schema(description = "Prescription date", example = "2026-03-30")
    private LocalDate prescriptionDate;
    @Schema(description = "Prescription notes", example = "Take medicines after food")
    @Size(max = 1000, message = "notes must be at most 1000 characters")
    private String notes;
    @ArraySchema(schema = @Schema(implementation = MedicineDTO.class), arraySchema = @Schema(description = "Prescribed medicines"))
    @Valid
    private List<MedicineDTO> medicines;


     public Prescription toEntity(){
            return new Prescription(
                this.id,
                this.patientId,
                this.doctorId,
                new Appointment(appointmentId),
                this.prescriptionDate,
                this.notes
            );
     }
}
