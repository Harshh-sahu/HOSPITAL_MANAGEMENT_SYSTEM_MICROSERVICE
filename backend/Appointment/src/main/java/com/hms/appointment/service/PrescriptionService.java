package com.hms.appointment.service;

import com.hms.appointment.dto.MedicineDTO;
import com.hms.appointment.dto.PrescriptionDTO;
import com.hms.appointment.dto.PrescriptionDetails;
import com.hms.appointment.exception.HmsException;

import java.util.List;

public interface PrescriptionService {

    public Long savePrescription(PrescriptionDTO Request);

    public  PrescriptionDTO getPrescriptionByAppointmentId(Long appointmentId);

    public PrescriptionDTO getPrescriptionById(Long prescriptionId);

    public List<PrescriptionDetails> getPrescriptionsByPatientId(Long patientId)throws HmsException;

    public List<PrescriptionDetails> getPrescriptions()throws HmsException;

    List<MedicineDTO> getMedicineByPatientId(Long patientId)throws HmsException;





}
