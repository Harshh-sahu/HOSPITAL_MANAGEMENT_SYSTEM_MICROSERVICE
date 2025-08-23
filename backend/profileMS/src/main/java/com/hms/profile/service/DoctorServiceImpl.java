package com.hms.profile.service;

import com.hms.profile.dto.DoctorDTO;
import com.hms.profile.exception.HmsException;
import com.hms.profile.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public Long addDoctor(DoctorDTO doctorDTO) {
        if (doctorDTO.getEmail() != null && doctorRepository.findByEmail(doctorDTO.getEmail()).isPresent()) {
            throw new RuntimeException("DOCTOR_ALREADY_EXISTS");
        }
        if (doctorDTO.getLicenseNo() != null && doctorRepository.findByLicenseNo(doctorDTO.getLicenseNo()).isPresent()) {
            throw new RuntimeException("DOCTOR_ALREADY_EXISTS");
        }
        return doctorRepository.save(doctorDTO.toEntity()).getId();
    }

    @Override
    public DoctorDTO getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DOCTOR_NOT_FOUND"))
                .toDTO();
    }

    @Override
    public DoctorDTO updateDoctor(DoctorDTO doctorDTO) throws HmsException {
        doctorRepository.findById(doctorDTO.getId())
                .orElseThrow(() -> new RuntimeException("DOCTOR_NOT_FOUND"))
                .toDTO();

        return doctorRepository.save(doctorDTO.toEntity()).toDTO();
    }
}
