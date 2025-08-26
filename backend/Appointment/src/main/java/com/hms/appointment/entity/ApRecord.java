package com.hms.appointment.entity;

import com.hms.appointment.dto.ApRecordDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private Long doctorId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private String symptoms;
    private String diagnosis;
    private String tests;
    private String notes;
    private String referral;

    private LocalDate followUpDate;
    private LocalDateTime createdAt;



    public ApRecordDTO toDTO(){
        return new ApRecordDTO(
                this.id,
                this.patientId,
                this.doctorId,
                this.appointment.getId() ,
                com.hms.appointment.utility.StringListConverter.convertStringToList(this.symptoms),
                this.diagnosis,
                com.hms.appointment.utility.StringListConverter.convertStringToList(this.tests),
                this.notes,
                this.referral,
                this.followUpDate,
                this.createdAt
        );
    }




}
