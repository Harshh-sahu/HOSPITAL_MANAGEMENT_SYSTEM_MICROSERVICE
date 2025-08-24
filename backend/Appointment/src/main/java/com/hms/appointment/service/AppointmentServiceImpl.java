package com.hms.appointment.service;

import com.hms.appointment.dto.*;
import com.hms.appointment.entity.Appointment;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private ApiService apiService;

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Override
    public Long scheduleAppointment(AppointmentDTO appointmentDTO) throws HmsException {

        Boolean doctorExists = apiService.doctorExist(appointmentDTO.getDoctorId()).block();
        if(doctorExists == null || !doctorExists) {
            throw new HmsException("DOCTOR_NOT_FOUND");
        }
        Boolean patientExists = apiService.patientExist(appointmentDTO.getPatientId()).block();
        if(patientExists == null || !patientExists) {
            throw new HmsException("PATIENT_NOT_FOUND");
        }
        appointmentDTO.setStatus(Status.SCHEDULED);;
      return  appointmentRepository.save(appointmentDTO.toEntity()).getId();

    }

    @Override
    public void cancelAppointment(Long appointmentId) throws HmsException {
        Appointment appointment =appointmentRepository.findById(appointmentId).orElseThrow(()-> new HmsException("APPOINTMENT_NOT_FOUND"));
        if (appointment.getStatus().equals(Status.CANCELLED)) {
            throw new HmsException("APPOINTMENT_ALREADY_CANCELLED");

        }
        appointment.setStatus(Status.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    public void completeAppointment(Long appointmentId) throws HmsException {

    }

    @Override
    public void rescheduleAppointment(Long appointmentId, String newDateTime) throws HmsException {

    }

    @Override
    public AppointmentDTO getAppointmentDetails(Long appointmentId) throws HmsException {

        return appointmentRepository.findById(appointmentId).orElseThrow(()-> new HmsException("APPOINTMENT_NOT_FOUND")).toDto();
    }

    @Override
    public AppointmentDetails getAppointmentDetailsWithName(Long appointmentId) throws HmsException {
     AppointmentDTO appointmentDTO=   appointmentRepository.findById(appointmentId).orElseThrow(()-> new HmsException("APPOINTMENT_NOT_FOUND")).toDto();

        DoctorDTO doctorDTO = apiService.getDoctorById(appointmentDTO.getDoctorId()).block();
        PatientDTO patientDTO = apiService.getPatientById(appointmentDTO.getPatientId()).block();
        if (doctorDTO == null || patientDTO == null) {
            throw new HmsException("DOCTOR_OR_PATIENT_NOT_FOUND");
        }
     return new AppointmentDetails(appointmentDTO.getId(),appointmentDTO.getPatientId(),appointmentDTO.getDoctorId(),patientDTO.getName(),doctorDTO.getName(),appointmentDTO.getAppointmentTime(),appointmentDTO.getStatus(),appointmentDTO.getReason(),appointmentDTO.getNotes(), patientDTO.getEmail(), patientDTO.getPhone());
    }
}
