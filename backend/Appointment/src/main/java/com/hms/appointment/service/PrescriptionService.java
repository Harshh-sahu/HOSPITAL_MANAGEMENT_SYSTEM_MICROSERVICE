package com.hms.appointment.service;

import com.hms.appointment.dto.PrescriptionDTO;

public interface PrescriptionService {

    public Long savePrescription(PrescriptionDTO Request);

    public  PrescriptionDTO getPrescriptionByAppointmentId(Long appointmentId);

    public PrescriptionDTO getPrescriptionById(Long prescriptionId);


}
