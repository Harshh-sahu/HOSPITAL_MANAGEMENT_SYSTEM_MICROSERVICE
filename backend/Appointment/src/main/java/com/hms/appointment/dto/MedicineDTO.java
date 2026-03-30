package com.hms.appointment.dto;

import com.hms.appointment.entity.Medicine;
import com.hms.appointment.entity.Prescription;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "MedicineDTO", description = "Medicine item in a prescription")
public class MedicineDTO {
    @Schema(description = "Internal medicine record id", example = "1")
    private Long id;
    @Schema(description = "Medicine name", example = "Paracetamol", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "name is required")
    @Size(max = 120, message = "name must be at most 120 characters")
    private String name;
    @Schema(description = "Master medicine id from medicine catalog", example = "10001")
    private Long medicineId;
    @Schema(description = "Dosage value", example = "500mg")
    @Size(max = 120, message = "dosage must be at most 120 characters")
    private String dosage;
    @Schema(description = "Frequency instruction", example = "TID")
    @Size(max = 60, message = "frequency must be at most 60 characters")
    private String frequency;
    @Schema(description = "Duration in days", example = "5")
    private  Integer duration;
    @Schema(description = "Administration route", example = "Oral")
    @Size(max = 60, message = "route must be at most 60 characters")
    private String route;
    @Schema(description = "Medicine type", example = "Tablet")
    @Size(max = 60, message = "type must be at most 60 characters")
    private String type;
    @Schema(description = "Special instructions", example = "After meals")
    @Size(max = 255, message = "instructions must be at most 255 characters")
    private String instructions;
    @Schema(description = "Prescription id", example = "7001")
    private Long prescriptionId;

    public Medicine toEntity(){
        return new Medicine(
                this.id,
                this.name,
                this.medicineId,
                this.dosage,
                this.frequency,
                this.duration,
                this.route,
                this.type,
                this.instructions,
                new Prescription(prescriptionId));

    }
}
