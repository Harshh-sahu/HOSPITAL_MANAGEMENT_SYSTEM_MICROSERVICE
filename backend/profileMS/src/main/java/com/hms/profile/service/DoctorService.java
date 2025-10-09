package com.hms.profile.service;

import com.hms.profile.dto.DoctorDTO;
import com.hms.profile.dto.DoctorDropdown;
import com.hms.profile.entity.Doctor;
import com.hms.profile.exception.HmsException;

import java.util.List;

public interface DoctorService {


    public Long addDoctor(DoctorDTO doctorDTO) throws HmsException;
public DoctorDTO getDoctorById(Long id)  throws HmsException;


public Boolean doctorExists(Long id) throws HmsException;

   public DoctorDTO updateDoctor(DoctorDTO doctorDTO) throws  HmsException;
   public List<DoctorDropdown> getDoctorDropdowns() throws HmsException;
   public List<DoctorDropdown> getDoctorsById(List<Long> ids)throws HmsException;

   public List<DoctorDTO> getAllDoctors() throws HmsException;
}
