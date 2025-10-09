package com.hms.profile.service;

import com.hms.profile.dto.DoctorDropdown;
import com.hms.profile.dto.PatientDTO;
import com.hms.profile.exception.HmsException;

import java.util.List;

public interface PatientService {

    public Long addPatient(PatientDTO patientDTO) throws HmsException;
    public PatientDTO getPatientById(Long id) throws HmsException;
public  Boolean patientExists(Long id) throws HmsException;
    public PatientDTO updatePatient(PatientDTO patientDTO) throws  HmsException;
    public List<PatientDTO> getAllPatients() throws HmsException;
    public List<DoctorDropdown> getPatientsById(List<Long> ids)throws HmsException;

}
