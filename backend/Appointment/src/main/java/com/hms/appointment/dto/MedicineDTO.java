package com.hms.appointment.dto;

import com.hms.appointment.entity.Medicine;
import com.hms.appointment.entity.Prescription;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineDTO {
    private Long id;
    private String name;
    private Long medicineId;
    private String dosage;
    private String frequency;
    private  Integer duration;
    private String route;
    private String type;
    private String instructions;
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
