package com.hms.appointment.entity;

import com.hms.appointment.dto.MedicineDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long medicineId;
    private String dosage;
    private String frequency;
    private  Integer duration;
    private String route;
    private String type;
    private String instructions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;


public MedicineDTO toDTO(){
    return new MedicineDTO(
            this.id,
            this.name,
            this.medicineId,
            this.dosage,
            this.frequency,
            this.duration,
            this.route,
            this.type,
            this.instructions,
            this.prescription.getId()
    );

}



}
