package com.hms.profile.service;

import com.hms.profile.dto.DoctorDropdown;
import com.hms.profile.dto.PatientDTO;
import com.hms.profile.entity.Patient;
import com.hms.profile.exception.HmsException;
import com.hms.profile.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService{

    @Autowired
    private PatientRepository patientRepository;
    @Override
    public Long addPatient(PatientDTO patientDTO) throws HmsException {
        if (patientDTO.getEmail() != null && patientRepository.findByEmail(patientDTO.getEmail()).isPresent()) {
            throw new HmsException("PATIENT_ALREADY_EXISTS");
        }
        // Null check lagaya aadharNo pe
        if (patientDTO.getAadharNo() != null && patientRepository.findByAadharNo(patientDTO.getAadharNo()).isPresent()) {
            throw new HmsException("PATIENT_ALREADY_EXISTS");
        }

        return patientRepository.save(patientDTO.toEntity()).getId();
    }

    @Override
    public PatientDTO getPatientById(Long id) throws HmsException {
       return patientRepository.findById(id).orElseThrow(()-> new HmsException("PATIENT_NOT_FOUND")).toDTO();
    }

    @Override
    public Boolean patientExists(Long id) throws HmsException {
return patientRepository.existsById(id);    }

    @Override
    public PatientDTO updatePatient(PatientDTO patientDTO) throws HmsException {

         patientRepository.findById(patientDTO.getId()).orElseThrow(()-> new HmsException("PATIENT_NOT_FOUND"));


        return patientRepository.save(patientDTO.toEntity()).toDTO();
    }

    @Override
    public List<PatientDTO> getAllPatients() throws HmsException {

        return ((List<Patient>) patientRepository.findAll()).stream().map(patient -> patient.toDTO()).toList();
    }

    @Override
    public List<DoctorDropdown> getPatientsById(List<Long> ids) throws HmsException {
        return patientRepository.findAllPatientsDropdownsByIds(ids);

    }
}
